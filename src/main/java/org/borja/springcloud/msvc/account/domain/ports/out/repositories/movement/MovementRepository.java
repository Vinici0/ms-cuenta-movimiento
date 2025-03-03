package org.borja.springcloud.msvc.account.domain.ports.out.repositories;

import org.borja.springcloud.msvc.account.domain.models.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

public interface MovementRepository {
    Mono<Movement> save(Movement movement);
    Flux<Movement> findAll();
    Mono<Movement> findById(Long id);
    Mono<Void> delete(Movement movement);
}