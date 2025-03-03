package org.borja.springcloud.msvc.account.domain.ports.out.repositories;

import org.borja.springcloud.msvc.account.domain.models.Movement;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface MovementQueryRepository {
    Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDate startDate, LocalDate endDate);
}