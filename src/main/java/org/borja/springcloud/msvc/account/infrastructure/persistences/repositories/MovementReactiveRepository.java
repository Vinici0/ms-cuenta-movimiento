package org.borja.springcloud.msvc.account.infrastructure.persistences.repositories;

import org.borja.springcloud.msvc.account.infrastructure.persistences.entities.MovementEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface MovementReactiveRepository extends ReactiveCrudRepository<MovementEntity, Long> {
    @Query("SELECT * FROM movements WHERE account_id = :accountId AND date BETWEEN :startDate AND :endDate")
    Flux<MovementEntity> findByAccountIdAndDateBetween(Long accountId, LocalDate startDate, LocalDate endDate);
}