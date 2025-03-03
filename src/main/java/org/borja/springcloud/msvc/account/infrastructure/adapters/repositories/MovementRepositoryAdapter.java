package org.borja.springcloud.msvc.account.infrastructure.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.domain.models.Movement;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.movement.MovementQueryRepository;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.movement.MovementRepository;
import org.borja.springcloud.msvc.account.infrastructure.persistences.entities.MovementEntity;
import org.borja.springcloud.msvc.account.infrastructure.persistences.repositories.MovementReactiveRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MovementRepositoryAdapter implements MovementRepository, MovementQueryRepository {

    private final MovementReactiveRepository reactiveRepository;

    @Override
    public Mono<Movement> save(Movement movement) {
        MovementEntity entity = MovementEntity.builder()
                .id(movement.getId())
                .date(movement.getDate())
                .movementType(movement.getMovementType())
                .amount(movement.getAmount())
                .balance(movement.getBalance())
                .accountId(movement.getAccountId())
                .build();
        return reactiveRepository.save(entity)
                .map(saved -> Movement.builder()
                        .id(saved.getId())
                        .date(saved.getDate())
                        .movementType(saved.getMovementType())
                        .amount(saved.getAmount())
                        .balance(saved.getBalance())
                        .accountId(saved.getAccountId())
                        .build());
    }

    @Override
    public Flux<Movement> findAll() {
        return reactiveRepository.findAll()
                .map(entity -> Movement.builder()
                        .id(entity.getId())
                        .date(entity.getDate())
                        .movementType(entity.getMovementType())
                        .amount(entity.getAmount())
                        .balance(entity.getBalance())
                        .accountId(entity.getAccountId())
                        .build());
    }

    @Override
    public Mono<Movement> findById(Long id) {
        return reactiveRepository.findById(id)
                .map(entity -> Movement.builder()
                        .id(entity.getId())
                        .date(entity.getDate())
                        .movementType(entity.getMovementType())
                        .amount(entity.getAmount())
                        .balance(entity.getBalance())
                        .accountId(entity.getAccountId())
                        .build());
    }

    @Override
    public Mono<Void> delete(Movement movement) {
        MovementEntity entity = MovementEntity.builder()
                .id(movement.getId())
                .date(movement.getDate())
                .movementType(movement.getMovementType())
                .amount(movement.getAmount())
                .balance(movement.getBalance())
                .accountId(movement.getAccountId())
                .build();
        return reactiveRepository.delete(entity);
    }

    @Override
    public Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDate startDate, LocalDate endDate) {
        return reactiveRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate)
                .map(entity -> Movement.builder()
                        .id(entity.getId())
                        .date(entity.getDate())
                        .movementType(entity.getMovementType())
                        .amount(entity.getAmount())
                        .balance(entity.getBalance())
                        .accountId(entity.getAccountId())
                        .build());
    }
}