package org.borja.springcloud.msvc.account.application.movement;

// Java y Terceros
import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.application.client.ClientResponseDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementReportDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementRequestDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementResponseDto;
import org.borja.springcloud.msvc.account.domain.models.Account;
import org.borja.springcloud.msvc.account.domain.models.Movement;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.account.AccountQueryRepository;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.account.AccountRepository;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.movement.MovementQueryRepository;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.movement.MovementRepository;
import org.borja.springcloud.msvc.account.domain.validator.MovementValidator;
import org.borja.springcloud.msvc.account.infrastructure.adapters.client.WebClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

// Spring y Reactor
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Clases del Proyecto
import org.borja.springcloud.msvc.account.domain.exceptions.InsufficientBalanceException;
import org.borja.springcloud.msvc.account.domain.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class MovementService implements IMovementService, IMovementReportService {

    private static final Logger log = LoggerFactory.getLogger(MovementService.class);
    private final MovementRepository movementRepository;
    private final MovementQueryRepository movementQueryRepositoryRepository;
    private final AccountRepository accountRepository;
    private final AccountQueryRepository accountQueryRepository;
    private final WebClientService webClientService;
    private final MovementValidator movementValidator;


    @Override
    public Mono<MovementResponseDto> addMovement(MovementRequestDto movRequest) {
        log.info("Adding new movement for account: {}", movRequest.getAccountNumber());
        return accountQueryRepository.findByAccountNumber(movRequest.getAccountNumber())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + movRequest.getAccountNumber())))
                .flatMap(account -> processMovement(account, movRequest));
    }

    private Mono<MovementResponseDto> processMovement(Account account, MovementRequestDto movRequest) {
        BigDecimal currentBalance = account.getInitialBalance();
        BigDecimal amount = movRequest.getAmount();
        BigDecimal newBalance = currentBalance.add(amount);

        return Mono.defer(() -> {
            movementValidator.validateBalance(currentBalance, amount, newBalance);
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
                    .doOnSuccess(mov -> log.info("Movement successfully added. New balance: {}", newBalance));
        }).onErrorMap(InsufficientBalanceException.class, e -> e);
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
                .flatMap(account -> Mono.defer(() -> {
                    BigDecimal revertedBalance = account.getInitialBalance().subtract(existingMovement.getAmount());
                    BigDecimal newBalance = revertedBalance.add(movRequest.getAmount());

                    movementValidator.validateBalance(revertedBalance, movRequest.getAmount(), newBalance);

                    account.setInitialBalance(newBalance);
                    existingMovement.setMovementType(movRequest.getMovementType());
                    existingMovement.setAmount(movRequest.getAmount());
                    existingMovement.setDate(LocalDate.now());
                    existingMovement.setBalance(revertedBalance);

                    return accountRepository.save(account)
                            .then(movementRepository.save(existingMovement))
                            .flatMap(this::mapToResponseDto)
                            .doOnSuccess(mov -> log.info("Movement successfully updated. New balance: {}", newBalance));
                }))
                .onErrorMap(InsufficientBalanceException.class, e -> e)
                .doOnError(error -> log.error("Error updating movement: {}", error.getMessage()));
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
                        accountQueryRepository.findByClientId(clientId)
                                .flatMap(account -> getReportForAccount(account, startDate, endDate, client))
                )
                .sort(Comparator.comparing(MovementReportDto::getFecha).reversed());
    }

    private Flux<MovementReportDto> getReportForAccount(Account account, LocalDate startDate, LocalDate endDate, ClientResponseDto client) {
        return movementQueryRepositoryRepository.findByAccountIdAndDateBetween(account.getId(), startDate, endDate)
                .sort(Comparator.comparing(Movement::getDate))
                .collectList()//Sirve para acumular los movimientos en una lista
                .flatMapMany(movements -> {
                    BigDecimal initialBalance = movements.isEmpty() ? account.getInitialBalance() : movements.get(0).getBalance();
                    BigDecimal[] accumulatedBalance = { initialBalance };

                    return Flux.fromIterable(movements)
                            .map(movement -> {
                                BigDecimal balanceBefore = accumulatedBalance[0];
                                BigDecimal newBalance = balanceBefore.add(movement.getAmount()); // Fixed line
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