package org.borja.springcloud.msvc.account.presentation.handler;

import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.application.movement.IMovementReportService;
import org.borja.springcloud.msvc.account.application.movement.IMovementService;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementReportDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementRequestDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MovementHandler {

    private final IMovementService movementService;
    private final IMovementReportService movementReportService;

    public Mono<ServerResponse> addMovement(ServerRequest request) {
        return request.bodyToMono(MovementRequestDto.class)
                .flatMap(movementService::addMovement)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto));
    }

    public Mono<ServerResponse> getAllMovements(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movementService.getAllMovements(), MovementResponseDto.class);
    }

    public Mono<ServerResponse> getMovementById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return movementService.getMovementById(id)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto));
    }

    public Mono<ServerResponse> updateMovement(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(MovementRequestDto.class)
                .flatMap(dto -> movementService.updateMovement(id, dto))
                .flatMap(updatedDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedDto));
    }

    public Mono<ServerResponse> deleteMovement(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return movementService.deleteMovement(id)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getReport(ServerRequest request) {
        LocalDate startDate = LocalDate.parse(request.queryParam("fechaInicio")
                .orElseThrow(() -> new IllegalArgumentException("fechaInicio is required")));
        LocalDate endDate = LocalDate.parse(request.queryParam("fechaFin")
                .orElseThrow(() -> new IllegalArgumentException("fechaFin is required")));
        Long clientId = Long.parseLong(request.queryParam("clientId")
                .orElseThrow(() -> new IllegalArgumentException("clientId is required")));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movementReportService.getCustomReport(startDate, endDate, clientId), MovementReportDto.class);
    }
}