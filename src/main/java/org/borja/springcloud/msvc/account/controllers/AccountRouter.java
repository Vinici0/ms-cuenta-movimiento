package org.borja.springcloud.msvc.account.controllers;

import lombok.RequiredArgsConstructor;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

import org.borja.springcloud.msvc.account.dtos.account.AccountRequestDto;
import org.borja.springcloud.msvc.account.dtos.account.AccountResponseDto;
import org.borja.springcloud.msvc.account.services.account.IAccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class AccountRouter {
    private final IAccountService accountService;

    @Bean
    public RouterFunction<ServerResponse> accountRoutes() {
        return RouterFunctions
                .route(GET("/api/cuentas").and(accept(MediaType.APPLICATION_JSON)),
                        request -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(accountService.getAllAccounts(), AccountResponseDto.class))

                .andRoute(GET("/api/cuentas/{accountNumber}"),
                        request -> accountService.getAccountByNumber(request.pathVariable("accountNumber"))
                                .flatMap(account -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(account)))

                .andRoute(PUT("/api/cuentas/{accountNumber}").and(accept(MediaType.APPLICATION_JSON)),
                        request -> request.bodyToMono(AccountRequestDto.class)
                                .flatMap(accountDto -> accountService.updateAccount(
                                        request.pathVariable("accountNumber"), accountDto))
                                .flatMap(account -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(account)))

                .andRoute(POST("/api/cuentas"),
                        request -> request.bodyToMono(AccountRequestDto.class)
                                .flatMap(accountDto -> accountService.addAccount(accountDto))
                                .flatMap(account -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(account)))


                .andRoute(DELETE("/api/cuentas/{accountNumber}"),
                        request -> accountService.deleteAccount(request.pathVariable("accountNumber"))
                                .then(ServerResponse.noContent().build()));
    }

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}