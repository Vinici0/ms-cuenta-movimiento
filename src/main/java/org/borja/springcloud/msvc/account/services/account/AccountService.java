package org.borja.springcloud.msvc.account.services.account;

// Java core imports
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.borja.springcloud.msvc.account.dtos.account.AccountRequestDto;
import org.borja.springcloud.msvc.account.dtos.account.AccountResponseDto;
import org.borja.springcloud.msvc.account.exceptions.ResourceNotFoundException;
import org.borja.springcloud.msvc.account.models.Account;
import org.borja.springcloud.msvc.account.models.Client;
import org.borja.springcloud.msvc.account.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;

    @Value("${topic}")
    String topic;

    @Autowired
    KafkaTemplate<String, Client> kafkaTemplate;

    @Override
    public Mono<AccountResponseDto> addAccount(AccountRequestDto accountDto) {
        log.info("Adding new account for client ID: {}", accountDto.getClientId());

        Client client = new Client();
        client.setId(accountDto.getClientId());
        CompletableFuture<SendResult<String, Client>> future = kafkaTemplate.send(topic, client);

        future.whenCompleteAsync((result, ex) -> {
            if (ex != null) {
                log.error("Error sending message: {}", ex.getMessage());
                throw new RuntimeException(ex);
            } else {
                log.info("Message sent: {}", result.getProducerRecord().value());
            }
        });
        Account account = new Account();
        account.setAccountType(accountDto.getAccountType());
        account.setInitialBalance(accountDto.getInitialBalance());
        account.setClientId(accountDto.getClientId());
        account.generateAccountNumber();

        return accountRepository.save(account)
                .map(this::mapToResponseDto)
                .doOnSuccess(accountResponseDto -> log.info("Account created successfully with account number: {}", accountResponseDto.getAccountNumber()));
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