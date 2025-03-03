package org.borja.springcloud.msvc.account.presentation.controllers;


import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.application.movement.IMovementReportService;
import org.borja.springcloud.msvc.account.application.movement.IMovementService;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementReportDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementRequestDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import java.time.LocalDate;

@RequiredArgsConstructor
@Configuration
public class MovementRouter {

    private final IMovementService movementService;
    private final IMovementReportService movementReportService;

    @Bean
    public RouterFunction<ServerResponse> movementRoutes() {
        return RouterFunctions
                .route(RequestPredicates.POST("/api/movimientos").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        request -> request.bodyToMono(MovementRequestDto.class)
                                .flatMap(movementService::addMovement)
                                .flatMap(dto -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(dto)))
                .andRoute(RequestPredicates.GET("/api/movimientos").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        request -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(movementService.getAllMovements(), MovementResponseDto.class))
                .andRoute(RequestPredicates.GET("/api/movimientos/{id}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        request -> movementService.getMovementById(Long.parseLong(request.pathVariable("id")))
                                .flatMap(dto -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(dto)))
                .andRoute(RequestPredicates.PUT("/api/movimientos/{id}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        request -> request.bodyToMono(MovementRequestDto.class)
                                .flatMap(dto -> movementService.updateMovement(Long.parseLong(request.pathVariable("id")), dto))
                                .flatMap(updatedDto -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(updatedDto)))
                .andRoute(RequestPredicates.DELETE("/api/movimientos/{id}"),
                        request -> movementService.deleteMovement(Long.parseLong(request.pathVariable("id")))
                                .then(ServerResponse.noContent().build()))
                .andRoute(RequestPredicates.GET("/api/reportes").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        request -> {
                            LocalDate startDate = LocalDate.parse(request.queryParam("fechaInicio")
                                    .orElseThrow(() -> new IllegalArgumentException("fechaInicio is required")));
                            LocalDate endDate = LocalDate.parse(request.queryParam("fechaFin")
                                    .orElseThrow(() -> new IllegalArgumentException("fechaFin is required")));
                            Long clientId = Long.parseLong(request.queryParam("clientId")
                                    .orElseThrow(() -> new IllegalArgumentException("clientId is required")));
                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(movementReportService.getCustomReport(startDate, endDate, clientId),
                                            MovementReportDto.class);
                        });
    }

}