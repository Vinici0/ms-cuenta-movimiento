package org.borja.springcloud.msvc.account.services.movement;

// Java core imports
import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.dtos.movement.MovementRequestDto;
import org.borja.springcloud.msvc.account.dtos.movement.MovementResponseDto;
import org.borja.springcloud.msvc.account.exceptions.InsufficientBalanceException;
import org.borja.springcloud.msvc.account.exceptions.ResourceNotFoundException;
import org.borja.springcloud.msvc.account.models.Account;
import org.borja.springcloud.msvc.account.models.Movement;
import org.borja.springcloud.msvc.account.repositories.AccountRepository;
import org.borja.springcloud.msvc.account.repositories.MovementRepository;
import org.borja.springcloud.msvc.account.repositories.interfaces.MovementReportProjection;
import org.borja.springcloud.msvc.account.validadors.MovementValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MovementService implements IMovementService {

    private static final Logger log = LoggerFactory.getLogger(MovementService.class);
    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final MovementValidator movementValidator;

    @Override
    public Mono<MovementResponseDto> addMovement(MovementRequestDto movRequest) {
        log.info("Adding new movement for account: {}", movRequest.getAccountNumber());

        return accountRepository.findByAccountNumber(movRequest.getAccountNumber())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Account not found: " + movRequest.getAccountNumber())))
                .flatMap(account -> {
                    // Obtener el saldo actual y el monto del movimiento
                    double currentBalance = account.getInitialBalance();
                    double amount = movRequest.getAmount();

                    // Calcular el nuevo saldo sumando el valor del movimiento (positivo o negativo)
                    double newBalance = calculateNewBalance(currentBalance, amount);

                    // Validar el balance antes de proceder
                    if (!movementValidator.isValidBalance(newBalance)) {
                        return Mono.error(new InsufficientBalanceException(
                                String.format("Insufficient balance. Current: %.2f, Requested: %.2f", currentBalance, amount)));
                    }

                    // Actualizar el saldo de la cuenta
                    account.setInitialBalance(newBalance);

                    // Crear el movimiento (se guarda el saldo anterior al movimiento)
                    Movement movement = Movement.builder()
                            .accountId(account.getId())
                            .date(LocalDate.now())
                            .movementType(movRequest.getMovementType())
                            .amount(amount)
                            .balance(currentBalance)
                            .build();

                    // Guardar la cuenta y el movimiento
                    return accountRepository.save(account)
                            .then(movementRepository.save(movement))
                            .flatMap(this::mapToResponseDto)
                            .doOnSuccess(mov -> log.info("Movement successfully added. New balance: {}", newBalance))
                            .doOnError(error -> log.error("Error adding movement: {}", error.getMessage()));
                });
    }

    @Override
    public Flux<MovementResponseDto> getAllMovements() {
        log.info("Fetching all movements");
        return movementRepository.findAll()
                .flatMap(this::mapToResponseDto);
    }

    @Override
    public Mono<MovementResponseDto> getMovementById(Long id) {
        log.info("Fetching movement with ID: {}", id);
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Movement not found with ID: " + id)))
                .flatMap(this::mapToResponseDto);
    }

    @Override
    public Mono<MovementResponseDto> updateMovement(Long id, MovementRequestDto movRequest) {
        log.info("Updating movement with ID: {} for account: {}", id, movRequest.getAccountNumber());

        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Movement not found with ID: " + id)))
                .flatMap(existingMovement -> {
                    return accountRepository.findById(existingMovement.getAccountId())
                            .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                                    "Account not found with ID: " + existingMovement.getAccountId())))
                            .flatMap(account -> {
                                // Revertir el efecto del movimiento anterior:
                                // Dado que se había sumado (o restado) el monto, para revertir basta con restarlo.
                                double balanceAfterReversal = reverseMovement(account.getInitialBalance(), existingMovement.getAmount());

                                // Calcular el nuevo saldo aplicando el nuevo movimiento
                                double newBalance = calculateNewBalance(balanceAfterReversal, movRequest.getAmount());

                                // Validar el saldo resultante
                                if (!movementValidator.isValidBalance(newBalance)) {
                                    return Mono.error(new InsufficientBalanceException(
                                            String.format("Insufficient balance. Current: %.2f, Requested: %.2f", balanceAfterReversal, movRequest.getAmount())
                                    ));
                                }

                                // Actualizar la cuenta y los detalles del movimiento
                                account.setInitialBalance(newBalance);
                                updateMovementDetails(existingMovement, movRequest, balanceAfterReversal);

                                // Guardar los cambios en transacción
                                return accountRepository.save(account)
                                        .then(movementRepository.save(existingMovement))
                                        .flatMap(this::mapToResponseDto)
                                        .doOnSuccess(mov -> log.info("Movement successfully updated. New balance: {}", newBalance))
                                        .doOnError(error -> log.error("Error updating movement: {}", error.getMessage()));
                            });
                });
    }

    // Para revertir el movimiento, se "deshace" su efecto restando el monto previamente aplicado.
    private double reverseMovement(double currentBalance, double amount) {
        return currentBalance - amount;
    }

    private void updateMovementDetails(Movement movement, MovementRequestDto request, double balance) {
        movement.setMovementType(request.getMovementType());
        movement.setAmount(request.getAmount());
        movement.setDate(LocalDate.now());
        movement.setBalance(balance);
    }

    @Override
    public Mono<Void> deleteMovement(Long id) {
        log.info("Deleting movement with ID: {}", id);
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Movement not found with ID: " + id)))
                .flatMap(movementRepository::delete);
    }

    @Override
    public Flux<MovementReportProjection> getCustomReport(LocalDate startDate, LocalDate endDate, Long clientId) {
        log.info("Generating movement report for client ID: {} from {} to {}", clientId, startDate, endDate);
        return movementRepository.findAllInRangeNative(startDate, endDate, clientId);
    }

    private Mono<MovementResponseDto> mapToResponseDto(Movement movement) {
        return accountRepository.findById(movement.getAccountId())
                .map(account -> MovementResponseDto.builder()
                        .id(movement.getId())
                        .date(movement.getDate())
                        .movementType(movement.getMovementType())
                        .amount(movement.getAmount())
                        .balance(movement.getBalance())
                        .accountNumber(account.getAccountNumber())
                        .build());
    }

    private Movement createNewMovement(Account account, MovementRequestDto request, double initialBalance) {
        return Movement.builder()
                .accountId(account.getId())
                .date(LocalDate.now())
                .movementType(request.getMovementType())
                .amount(request.getAmount())
                .balance(initialBalance)
                .build();
    }

    // Método para calcular el nuevo saldo:
    // Se suma el valor del movimiento (positivo para depósitos, negativo para retiros).
    private double calculateNewBalance(double currentBalance, double amount) {
        return currentBalance + amount;
    }
}
