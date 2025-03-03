package org.borja.springcloud.msvc.account.infrastructure.persistences.repositories;

import org.borja.springcloud.msvc.account.infrastructure.persistences.entities.AccountEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountReactiveRepository extends ReactiveCrudRepository<AccountEntity, Long> {
    Mono<org.borja.springcloud.msvc.account.infrastructure.persistences.entities.AccountEntity> findByAccountNumber(String accountNumber);
    Flux<AccountEntity> findAccountsByStatus(Boolean status);
    Flux<AccountEntity> findByClientId(Long clientId);
}