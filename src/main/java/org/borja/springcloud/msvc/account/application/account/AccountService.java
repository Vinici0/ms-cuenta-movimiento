package org.borja.springcloud.msvc.account.application.account;

import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.application.account.dtos.AccountRequestDto;
import org.borja.springcloud.msvc.account.application.account.dtos.AccountResponseDto;
import org.borja.springcloud.msvc.account.application.exceptions.ResourceNotFoundException;
import org.borja.springcloud.msvc.account.domain.models.Account;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.account.AccountQueryRepository;
import org.borja.springcloud.msvc.account.domain.ports.out.repositories.account.AccountRepository;
import org.borja.springcloud.msvc.account.infrastructure.adapters.client.WebClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final AccountQueryRepository accountQueryRepository;
    private final WebClientService webClientService;

    @Value("${microservice.clients.url}")
    private String clientsServiceUrl;

    @Override
    public Mono<AccountResponseDto> addAccount(AccountRequestDto accountDto) {
        return webClientService.findClientById(accountDto.getClientId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Client not found with ID: " + accountDto.getClientId())))
                .flatMap(client -> {
                    Account account = new Account();
                    account.setAccountType(accountDto.getAccountType());
                    account.setInitialBalance(accountDto.getInitialBalance());
                    account.setClientId(accountDto.getClientId());
                    account.generateAccountNumber();
                    return accountRepository.save(account)
                            .map(this::mapToResponseDto);
                });
    }

    @Override
    public Flux<AccountResponseDto> getAllAccounts() {
        return accountQueryRepository.findAccountsByStatus(true)
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<AccountResponseDto> getAccountByNumber(String accountNumber) {
        return accountQueryRepository.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found with number: " + accountNumber)))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<AccountResponseDto> updateAccount(String accountNumber, AccountRequestDto accountDto) {
        return accountQueryRepository.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found.")))
                .map(existing -> {
                    existing.setAccountType(accountDto.getAccountType());
                    existing.setInitialBalance(accountDto.getInitialBalance());
                    existing.setStatus(accountDto.getStatus());
                    existing.setClientId(accountDto.getClientId());
                    return existing;
                })
                .flatMap(accountRepository::save)
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<Void> deleteAccount(String accountNumber) {
        return accountQueryRepository.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found with number: " + accountNumber)))
                .flatMap(existing -> {
                    existing.setStatus(false);
                    return accountRepository.save(existing);
                })
                .then();
    }

    private AccountResponseDto mapToResponseDto(Account account) {
        return AccountResponseDto.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .initialBalance(account.getInitialBalance())
                .clientId(account.getClientId())
                .build();
    }
}