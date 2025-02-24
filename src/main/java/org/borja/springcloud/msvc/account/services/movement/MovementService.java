package org.borja.springcloud.msvc.account.services.movement;

// Java y Terceros
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

// Spring y Reactor
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Clases del Proyecto
import org.borja.springcloud.msvc.account.dtos.client.ClientResponseDto;
import org.borja.springcloud.msvc.account.dtos.movement.MovementReportDto;
import org.borja.springcloud.msvc.account.dtos.movement.MovementRequestDto;
import org.borja.springcloud.msvc.account.dtos.movement.MovementResponseDto;
import org.borja.springcloud.msvc.account.exceptions.InsufficientBalanceException;
import org.borja.springcloud.msvc.account.exceptions.ResourceNotFoundException;
import org.borja.springcloud.msvc.account.models.Account;
import org.borja.springcloud.msvc.account.models.Movement;
import org.borja.springcloud.msvc.account.repositories.AccountRepository;
import org.borja.springcloud.msvc.account.repositories.MovementRepository;
import org.borja.springcloud.msvc.account.services.client.WebClientService;
import org.borja.springcloud.msvc.account.validadors.MovementValidator;

@Service
@RequiredArgsConstructor
public class MovementService implements IMovementService {

    private static final Logger log = LoggerFactory.getLogger(MovementService.class);
    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final MovementValidator movementValidator;
    private final WebClientService webClientService;

    @Override
    public Mono<MovementResponseDto> addMovement(MovementRequestDto movRequest) {
        log.info("Adding new movement for account: {}", movRequest.getAccountNumber());
        return accountRepository.findByAccountNumber(movRequest.getAccountNumber())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + movRequest.getAccountNumber())))
                .flatMap(account -> processMovement(account, movRequest));
    }

    private Mono<MovementResponseDto> processMovement(Account account, MovementRequestDto movRequest) {
        double currentBalance = account.getInitialBalance();
        double amount = movRequest.getAmount();
        double newBalance = currentBalance + amount;

        if (!movementValidator.isValidBalance(newBalance)) {
            return Mono.error(new InsufficientBalanceException(
                    String.format("Insufficient balance. Current: %.2f, Requested: %.2f", currentBalance, amount)
            ));
        }

        account.setInitialBalance(newBalance);

        Movement movement = Movement.builder()
                .accountId(account.getId())
                .date(LocalDate.now())
                .movementType(movRequest.getMovementType())
                .amount(amount)
                .balance(currentBalance)
                .build();

        return accountRepository.save(account)
                .then(movementRepository.save(movement))
                .flatMap(this::mapToResponseDto)
                .doOnSuccess(mov -> log.info("Movement successfully added. New balance: {}", newBalance))
                .doOnError(error -> log.error("Error adding movement: {}", error.getMessage()));
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
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Movement not found with ID: " + id)))
                .flatMap(this::mapToResponseDto);
    }

    @Override
    public Mono<MovementResponseDto> updateMovement(Long id, MovementRequestDto movRequest) {
        log.info("Updating movement with ID: {} for account: {}", id, movRequest.getAccountNumber());
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Movement not found with ID: " + id)))
                .flatMap(existingMovement -> updateExistingMovement(existingMovement, movRequest));
    }

    private Mono<MovementResponseDto> updateExistingMovement(Movement existingMovement, MovementRequestDto movRequest) {
        return accountRepository.findById(existingMovement.getAccountId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found with ID: " + existingMovement.getAccountId())))
                .flatMap(account -> {
                    // Revertir el efecto del movimiento anterior
                    double revertedBalance = account.getInitialBalance() - existingMovement.getAmount();
                    double newBalance = revertedBalance + movRequest.getAmount();

                    if (!movementValidator.isValidBalance(newBalance)) {
                        return Mono.error(new InsufficientBalanceException(
                                String.format("Insufficient balance. Current: %.2f, Requested: %.2f", revertedBalance, movRequest.getAmount())
                        ));
                    }

                    account.setInitialBalance(newBalance);
                    existingMovement.setMovementType(movRequest.getMovementType());
                    existingMovement.setAmount(movRequest.getAmount());
                    existingMovement.setDate(LocalDate.now());
                    existingMovement.setBalance(revertedBalance);

                    return accountRepository.save(account)
                            .then(movementRepository.save(existingMovement))
                            .flatMap(this::mapToResponseDto)
                            .doOnSuccess(mov -> log.info("Movement successfully updated. New balance: {}", newBalance))
                            .doOnError(error -> log.error("Error updating movement: {}", error.getMessage()));
                });
    }

    @Override
    public Mono<Void> deleteMovement(Long id) {
        log.info("Deleting movement with ID: {}", id);
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Movement not found with ID: " + id)))
                .flatMap(movementRepository::delete);
    }

    @Override
    public Flux<MovementReportDto> getCustomReport(LocalDate startDate, LocalDate endDate, Long clientId) {
        log.info("Generating movement report for client ID: {} from {} to {}", clientId, startDate, endDate);
        return webClientService.findClientById(clientId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Client not found: " + clientId)))
                .flatMapMany(client ->
                        accountRepository.findByClientId(clientId)
                                .flatMap(account -> getReportForAccount(account, startDate, endDate, client))
                )
                .sort(Comparator.comparing(MovementReportDto::getFecha).reversed());
    }

    private Flux<MovementReportDto> getReportForAccount(Account account, LocalDate startDate, LocalDate endDate, ClientResponseDto client) {
        return movementRepository.findByAccountIdAndDateBetween(account.getId(), startDate, endDate)
                .sort(Comparator.comparing(Movement::getDate))
                .collectList()
                .flatMapMany(movements -> {
                    double initialBalance = movements.isEmpty() ? account.getInitialBalance() : movements.get(0).getBalance();
                    double[] accumulatedBalance = { initialBalance };

                    return Flux.fromIterable(movements)
                            .map(movement -> {
                                double balanceBefore = accumulatedBalance[0];
                                double newBalance = balanceBefore + movement.getAmount();
                                accumulatedBalance[0] = newBalance;
                                return MovementReportDto.builder()
                                        .fecha(movement.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                        .cliente(client.getName())
                                        .numeroCuenta(account.getAccountNumber())
                                        .tipo(account.getAccountType().toString())
                                        .saldoInicial(balanceBefore)
                                        .estado(account.getStatus())
                                        .movimiento(movement.getAmount())
                                        .saldoDisponible(newBalance)
                                        .build();
                            });
                });
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
}