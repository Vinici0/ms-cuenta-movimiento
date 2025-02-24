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
                    double initialBalance = account.getInitialBalance();
                    double newBalance = initialBalance + movRequest.getAmount();

                    if (!movementValidator.isValidBalance(newBalance)) {
                        return Mono.error(new InsufficientBalanceException("Insufficient balance"));
                    }

                    account.setInitialBalance(newBalance);
                    Movement movement = createNewMovement(account, movRequest, initialBalance);

                    return accountRepository.save(account)
                            .then(movementRepository.save(movement))
                            .map(this::mapToResponseDto);
                })
                .doOnSuccess(mov -> log.info("Movement added successfully"));
    }

    @Override
    public Flux<MovementResponseDto> getAllMovements() {
        log.info("Fetching all movements");
        return movementRepository.findAll()
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<MovementResponseDto> getMovementById(Long id) {
        log.info("Fetching movement with ID: {}", id);
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Movement not found with ID: " + id)))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<MovementResponseDto> updateMovement(Long id, MovementRequestDto movRequest) {
        log.info("Updating movement with ID: {}", id);
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Movement not found with ID: " + id)))
                .flatMap(existingMovement -> {
                    Account account = existingMovement.getAccount();
                    double balanceAfterReversal = account.getInitialBalance() - existingMovement.getAmount();
                    double newBalance = balanceAfterReversal + movRequest.getAmount();

                    if (!movementValidator.isValidBalance(newBalance)) {
                        return Mono.error(new InsufficientBalanceException("Insufficient balance"));
                    }

                    account.setInitialBalance(newBalance);
                    updateMovementDetails(existingMovement, movRequest, balanceAfterReversal);

                    return accountRepository.save(account)
                            .then(movementRepository.save(existingMovement))
                            .map(this::mapToResponseDto);
                });
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

    private MovementResponseDto mapToResponseDto(Movement movement) {
        return MovementResponseDto.builder()
                .id(movement.getId())
                .date(movement.getDate())
                .movementType(movement.getMovementType())
                .amount(movement.getAmount())
                .balance(movement.getBalance())
                .accountNumber(movement.getAccount().getAccountNumber())
                .build();
    }

    private Movement createNewMovement(Account account, MovementRequestDto request, double initialBalance) {
        return Movement.builder()
                .account(account)
                .date(LocalDate.now())
                .movementType(request.getMovementType())
                .amount(request.getAmount())
                .balance(initialBalance)
                .build();
    }

    private void updateMovementDetails(Movement movement, MovementRequestDto request, double balance) {
        movement.setMovementType(request.getMovementType());
        movement.setAmount(request.getAmount());
        movement.setDate(LocalDate.now());
        movement.setBalance(balance);
    }
}