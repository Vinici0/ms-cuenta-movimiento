package org.borja.springcloud.msvc.account.repositories;

import org.borja.springcloud.msvc.account.models.Movement;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface MovementRepository extends ReactiveCrudRepository<Movement, Long> {

    @Query("SELECT * FROM movements WHERE account_id = :accountId AND date BETWEEN :startDate AND :endDate")
    Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDate startDate, LocalDate endDate);

}