package org.borja.springcloud.msvc.account.controllers;

import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.dtos.movement.MovementReportDto;
import org.borja.springcloud.msvc.account.dtos.movement.MovementRequestDto;
import org.borja.springcloud.msvc.account.dtos.movement.MovementResponseDto; 
import org.borja.springcloud.msvc.account.services.movement.IMovementService;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.time.LocalDate;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
@RequiredArgsConstructor
public class MovementRouter {

    private final IMovementService movementService;

    @Bean
    public RouterFunction<ServerResponse> movementRoutes() {
        return RouterFunctions
                .route(POST("/api/movimientos").and(accept(MediaType.APPLICATION_JSON)),
                        request -> request.bodyToMono(MovementRequestDto.class)
                                .flatMap(movementService::addMovement)
                                .flatMap(dto -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(dto)))

                .andRoute(GET("/api/movimientos").and(accept(MediaType.APPLICATION_JSON)),
                        request -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(movementService.getAllMovements(), MovementResponseDto.class))

                .andRoute(GET("/api/movimientos/{id}").and(accept(MediaType.APPLICATION_JSON)),
                        request -> movementService.getMovementById(Long.parseLong(request.pathVariable("id")))
                                .flatMap(dto -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(dto)))

                .andRoute(PUT("/api/movimientos/{id}").and(accept(MediaType.APPLICATION_JSON)),
                        request -> request.bodyToMono(MovementRequestDto.class)
                                .flatMap(dto -> movementService.updateMovement(
                                        Long.parseLong(request.pathVariable("id")), dto))
                                .flatMap(updatedDto -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(updatedDto)))

                .andRoute(DELETE("/api/movimientos/{id}"),
                        request -> movementService.deleteMovement(Long.parseLong(request.pathVariable("id")))
                                .then(ServerResponse.noContent().build()))

                .andRoute(GET("/api/reportes").and(accept(MediaType.APPLICATION_JSON)),
                        request -> {
                            LocalDate startDate = LocalDate.parse(request.queryParam("fechaInicio")
                                    .orElseThrow(() -> new IllegalArgumentException("fechaInicio is required")));
                            LocalDate endDate = LocalDate.parse(request.queryParam("fechaFin")
                                    .orElseThrow(() -> new IllegalArgumentException("fechaFin is required")));
                            Long clientId = Long.parseLong(request.queryParam("clientId")
                                    .orElseThrow(() -> new IllegalArgumentException("clientId is required")));

                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(movementService.getCustomReport(startDate, endDate, clientId),
                                            MovementReportDto.class);
                        });
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}