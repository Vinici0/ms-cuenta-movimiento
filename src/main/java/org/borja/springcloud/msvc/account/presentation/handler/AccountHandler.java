package org.borja.springcloud.msvc.account.presentation.handler;

import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.application.account.IAccountService;
import org.borja.springcloud.msvc.account.application.account.dtos.AccountRequestDto;
import org.borja.springcloud.msvc.account.application.account.dtos.AccountResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountHandler {

    private final IAccountService accountService;

    public Mono<ServerResponse> getAllAccounts(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(accountService.getAllAccounts(), AccountResponseDto.class);
    }

    public Mono<ServerResponse> getAccountByNumber(ServerRequest request) {
        String accountNumber = request.pathVariable("accountNumber");
        return accountService.getAccountByNumber(accountNumber)
                .flatMap(account -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(account));
    }

    public Mono<ServerResponse> updateAccount(ServerRequest request) {
        String accountNumber = request.pathVariable("accountNumber");
        return request.bodyToMono(AccountRequestDto.class)
                .flatMap(accountDto -> accountService.updateAccount(accountNumber, accountDto))
                .flatMap(account -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(account));
    }

    public Mono<ServerResponse> addAccount(ServerRequest request) {
        return request.bodyToMono(AccountRequestDto.class)
                .flatMap(accountService::addAccount)
                .flatMap(account -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(account));
    }

    public Mono<ServerResponse> deleteAccount(ServerRequest request) {
        String accountNumber = request.pathVariable("accountNumber");
        return accountService.deleteAccount(accountNumber)
                .then(ServerResponse.noContent().build());
    }
}