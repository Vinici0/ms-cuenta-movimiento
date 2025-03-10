package org.borja.springcloud.msvc.account.presentation.controllers;


import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.application.movement.IMovementReportService;
import org.borja.springcloud.msvc.account.application.movement.IMovementService;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementReportDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementRequestDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementResponseDto;
import org.borja.springcloud.msvc.account.presentation.handler.MovementHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class MovementRouter {

    private final MovementHandler movementHandler;

    @Bean
    public RouterFunction<ServerResponse> movementRoutes() {
        return RouterFunctions
                .route(RequestPredicates.POST("/api/movimientos")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        movementHandler::addMovement)
                .andRoute(RequestPredicates.GET("/api/movimientos")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        movementHandler::getAllMovements)
                .andRoute(RequestPredicates.GET("/api/movimientos/{id}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        movementHandler::getMovementById)
                .andRoute(RequestPredicates.PUT("/api/movimientos/{id}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        movementHandler::updateMovement)
                .andRoute(RequestPredicates.DELETE("/api/movimientos/{id}"),
                        movementHandler::deleteMovement)
                .andRoute(RequestPredicates.GET("/api/reportes")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        movementHandler::getReport);
    }
}