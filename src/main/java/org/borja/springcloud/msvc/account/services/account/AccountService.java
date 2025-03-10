package org.borja.springcloud.msvc.account.services.account;


import lombok.RequiredArgsConstructor;

import org.borja.springcloud.msvc.account.dtos.account.AccountRequestDto;
import org.borja.springcloud.msvc.account.dtos.account.AccountResponseDto;
import org.borja.springcloud.msvc.account.exceptions.ResourceNotFoundException;
import org.borja.springcloud.msvc.account.models.Account;

import org.borja.springcloud.msvc.account.repositories.AccountRepository;
import org.borja.springcloud.msvc.account.services.client.WebClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final WebClientService webClientService;

    @Value("${microservice.clients.url}")
    private String clientsServiceUrl;

    @Override
    public Mono<AccountResponseDto> addAccount(AccountRequestDto accountDto) {
        log.info("Creating new account for client: {}", accountDto.getClientId());

        return webClientService.findClientById(accountDto.getClientId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Client not found with ID: " + accountDto.getClientId())))
                .flatMap(client -> {
                    System.out.println("Client: " + client);
                    Account account = new Account();
                    account.setAccountType(accountDto.getAccountType());
                    account.setInitialBalance(accountDto.getInitialBalance());
                    account.setClientId(accountDto.getClientId());
                    account.generateAccountNumber();

                    return accountRepository.save(account)
                            .map(this::mapToResponseDto)
                            .doOnSuccess(accountResponseDto ->
                                    log.info("Account created successfully with account number: {}",
                                            accountResponseDto.getAccountNumber()));
                });
    }

    @Override
    public Flux<AccountResponseDto> getAllAccounts() {
        log.info("Fetching all accounts");
        return accountRepository.findAccountsByStatus(true)
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<AccountResponseDto> getAccountByNumber(String accountNumber) {
        log.info("Fetching account with number: {}", accountNumber);
        return accountRepository.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Account not found with number: " + accountNumber)))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<AccountResponseDto> updateAccount(String accountNumber, AccountRequestDto accountDto) {
        return accountRepository.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found.")))
                .map(existing -> {
                    Long storedId = existing.getId();
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
        log.info("Disabling account with number: {}", accountNumber);
        return accountRepository.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Account not found with number: " + accountNumber)))
                .flatMap(existingAccount -> {
                    existingAccount.setStatus(false);
                    return accountRepository.save(existingAccount);
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