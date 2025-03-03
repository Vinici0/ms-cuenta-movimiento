package org.borja.springcloud.msvc.account.domain.ports.out.repositories.account;

import org.borja.springcloud.msvc.account.domain.models.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountQueryRepository {
    Mono<Account> findByAccountNumber(String accountNumber);
    Flux<Account> findAccountsByStatus(Boolean status);
    Flux<Account> findByClientId(Long clientId);
}
