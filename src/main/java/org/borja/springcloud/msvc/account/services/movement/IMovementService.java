package org.borja.springcloud.msvc.account.services.movement;

import org.borja.springcloud.msvc.account.dtos.movement.MovementReportDto;
import org.borja.springcloud.msvc.account.dtos.movement.MovementRequestDto;
import org.borja.springcloud.msvc.account.dtos.movement.MovementResponseDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface IMovementService {
    Mono<MovementResponseDto> addMovement(MovementRequestDto movRequest);

    Flux<MovementResponseDto> getAllMovements();

    Mono<MovementResponseDto> getMovementById(Long id);

    Mono<MovementResponseDto> updateMovement(Long id, MovementRequestDto movRequest);

    Mono<Void> deleteMovement(Long id);

    Flux<MovementReportDto> getCustomReport(LocalDate startDate, LocalDate endDate, Long clientId);

}