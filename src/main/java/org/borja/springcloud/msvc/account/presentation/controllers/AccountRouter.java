package org.borja.springcloud.msvc.account.presentation.controllers;


import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.application.account.IAccountService;
import org.borja.springcloud.msvc.account.application.account.dtos.AccountRequestDto;
import org.borja.springcloud.msvc.account.application.account.dtos.AccountResponseDto;
import org.borja.springcloud.msvc.account.presentation.handler.AccountHandler;
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

    private final AccountHandler accountHandler;

    @Bean
    public RouterFunction<ServerResponse> accountRoutes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/api/cuentas")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        accountHandler::getAllAccounts)
                .andRoute(RequestPredicates.GET("/api/cuentas/{accountNumber}"),
                        accountHandler::getAccountByNumber)
                .andRoute(RequestPredicates.PUT("/api/cuentas/{accountNumber}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        accountHandler::updateAccount)
                .andRoute(RequestPredicates.POST("/api/cuentas"),
                        accountHandler::addAccount)
                .andRoute(RequestPredicates.DELETE("/api/cuentas/{accountNumber}"),
                        accountHandler::deleteAccount);
    }
}