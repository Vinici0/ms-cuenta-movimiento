package org.borja.springcloud.msvc.account.domain.ports.out.repositories.client;

import org.borja.springcloud.msvc.account.application.client.ClientResponseDto;
import reactor.core.publisher.Mono;

public interface IWebClientService {
    Mono<ClientResponseDto> findClientById(Long clientId);
}