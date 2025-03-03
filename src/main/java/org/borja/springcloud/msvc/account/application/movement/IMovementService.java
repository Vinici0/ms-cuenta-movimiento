package org.borja.springcloud.msvc.account.application.movement;

import org.borja.springcloud.msvc.account.application.movement.dtos.MovementReportDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementRequestDto;
import org.borja.springcloud.msvc.account.application.movement.dtos.MovementResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface IMovementService {
    Mono<MovementResponseDto> addMovement(MovementRequestDto movRequest);

    Flux<MovementResponseDto> getAllMovements();

    Mono<MovementResponseDto> getMovementById(Long id);

    Mono<MovementResponseDto> updateMovement(Long id, MovementRequestDto movRequest);

    Mono<Void> deleteMovement(Long id);


}