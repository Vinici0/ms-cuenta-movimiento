package org.borja.springcloud.msvc.account.application.movement;

import org.borja.springcloud.msvc.account.application.movement.dtos.MovementReportDto;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface IMovementReportService {
    Flux<MovementReportDto> getCustomReport(LocalDate startDate, LocalDate endDate, Long clientId);
}