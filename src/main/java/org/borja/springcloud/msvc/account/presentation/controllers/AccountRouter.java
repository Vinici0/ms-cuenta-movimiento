package org.borja.springcloud.msvc.account.presentation.controllers;


import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.application.account.IAccountService;
import org.borja.springcloud.msvc.account.application.account.dtos.AccountRequestDto;
import org.borja.springcloud.msvc.account.application.account.dtos.AccountResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class AccountRouter {

    private final IAccountService accountService;

    @Bean
    public RouterFunction<ServerResponse> accountRoutes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/api/cuentas").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        request -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(accountService.getAllAccounts(), AccountResponseDto.class))
                .andRoute(RequestPredicates.GET("/api/cuentas/{accountNumber}"),
                        request -> accountService.getAccountByNumber(request.pathVariable("accountNumber"))
                                .flatMap(account -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(account)))
                .andRoute(RequestPredicates.PUT("/api/cuentas/{accountNumber}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        request -> request.bodyToMono(AccountRequestDto.class)
                                .flatMap(accountDto -> accountService.updateAccount(request.pathVariable("accountNumber"), accountDto))
                                .flatMap(account -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(account)))
                .andRoute(RequestPredicates.POST("/api/cuentas"),
                        request -> request.bodyToMono(AccountRequestDto.class)
                                .flatMap(accountService::addAccount)
                                .flatMap(account -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(account)))
                .andRoute(RequestPredicates.DELETE("/api/cuentas/{accountNumber}"),
                        request -> accountService.deleteAccount(request.pathVariable("accountNumber"))
                                .then(ServerResponse.noContent().build()));
    }
}