package org.borja.springcloud.msvc.account.infrastructure.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.domain.models.Account;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.account.AccountQueryRepository;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.account.AccountRepository;
import org.borja.springcloud.msvc.account.infrastructure.persistences.entities.AccountEntity;
import org.borja.springcloud.msvc.account.infrastructure.persistences.repositories.AccountReactiveRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository, AccountQueryRepository {

    private final AccountReactiveRepository reactiveRepository;

    @Override
    public Mono<Account> findById(Long id) {
        return null;
    }

    @Override
    public Mono<Account> save(Account account) {
        AccountEntity entity = AccountEntity.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .initialBalance(account.getInitialBalance())
                .status(account.getStatus())
                .clientId(account.getClientId())
                .build();
        return reactiveRepository.save(entity)
                .map(saved -> Account.builder()
                        .id(saved.getId())
                        .accountNumber(saved.getAccountNumber())
                        .accountType(saved.getAccountType())
                        .initialBalance(saved.getInitialBalance())
                        .status(saved.getStatus())
                        .clientId(saved.getClientId())
                        .build());
    }

    @Override
    public Mono<Account> findByAccountNumber(String accountNumber) {
        return reactiveRepository.findByAccountNumber(accountNumber)
                .map(entity -> Account.builder()
                        .id(entity.getId())
                        .accountNumber(entity.getAccountNumber())
                        .accountType(entity.getAccountType())
                        .initialBalance(entity.getInitialBalance())
                        .status(entity.getStatus())
                        .clientId(entity.getClientId())
                        .build());
    }

    @Override
    public Flux<Account> findAccountsByStatus(Boolean status) {
        return reactiveRepository.findAccountsByStatus(status)
                .map(entity -> Account.builder()
                        .id(entity.getId())
                        .accountNumber(entity.getAccountNumber())
                        .accountType(entity.getAccountType())
                        .initialBalance(entity.getInitialBalance())
                        .status(entity.getStatus())
                        .clientId(entity.getClientId())
                        .build());
    }

    @Override
    public Flux<Account> findByClientId(Long clientId) {
        return reactiveRepository.findByClientId(clientId)
                .map(entity -> Account.builder()
                        .id(entity.getId())
                        .accountNumber(entity.getAccountNumber())
                        .accountType(entity.getAccountType())
                        .initialBalance(entity.getInitialBalance())
                        .status(entity.getStatus())
                        .clientId(entity.getClientId())
                        .build());
    }
}