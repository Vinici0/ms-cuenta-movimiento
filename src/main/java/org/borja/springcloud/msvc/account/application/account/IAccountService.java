package org.borja.springcloud.msvc.account.application.account;

import org.borja.springcloud.msvc.account.application.account.dtos.AccountRequestDto;
import org.borja.springcloud.msvc.account.application.account.dtos.AccountResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountService {
    Mono<AccountResponseDto> addAccount(AccountRequestDto accountDto);
    Flux<AccountResponseDto> getAllAccounts();
    Mono<AccountResponseDto> getAccountByNumber(String accountNumber);
    Mono<AccountResponseDto> updateAccount(String accountNumber, AccountRequestDto accountDto);
    Mono<Void> deleteAccount(String accountNumber);
}