package org.borja.springcloud.msvc.account.repositories;

import org.borja.springcloud.msvc.account.models.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    Mono<Account> findByAccountNumber(String accountNumber);
    Flux<Account> findAccountsByStatus(Boolean status);
    Mono<Account> findByIdAndStatus(Long id, Boolean status);
}