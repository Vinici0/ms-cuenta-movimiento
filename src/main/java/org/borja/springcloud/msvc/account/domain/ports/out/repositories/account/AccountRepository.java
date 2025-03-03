package org.borja.springcloud.msvc.account.domain.ports.out.repositories.account;

import org.borja.springcloud.msvc.account.domain.models.Account;
import reactor.core.publisher.Mono;

public interface AccountRepository {
    Mono<Account> findById(Long id);
    Mono<Account> save(Account account);
}
