package org.borja.springcloud.msvc.account.infrastructure.adapters.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.borja.springcloud.msvc.account.application.client.ClientResponseDto;
import org.borja.springcloud.msvc.account.application.exceptions.ResourceNotFoundException;
import org.borja.springcloud.msvc.account.presentation.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientService {
    private static final Logger log = LoggerFactory.getLogger(WebClientService.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public WebClientService(@Value("${microservice.clients.url}") String clientsServiceUrl,
                            ObjectMapper objectMapper) {
        this.webClient = WebClient.create(clientsServiceUrl);
        this.objectMapper = objectMapper;
    }

    public Mono<ClientResponseDto> findClientById(Long clientId) {
        log.info("Llamando al servicio de clientes para el ID: {}", clientId);
        return webClient.get()
                .uri("/api/clientes/{id}", clientId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new ResourceNotFoundException("Error al llamar al servicio de clientes para el ID: " + clientId)))
                .bodyToMono(ApiResponse.class)//Se espera un ApiResponse
                .flatMap(response -> {
                    if (response.getData() == null) {
                        return Mono.error(new ResourceNotFoundException("Cliente no encontrado con ID: " + clientId));
                    }
                    try {
                        ClientResponseDto client = objectMapper.convertValue(response.getData(), ClientResponseDto.class);
                        return Mono.just(client);
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new ResourceNotFoundException("Error al mapear datos del cliente: " + e.getMessage()));
                    }
                })
                .doOnError(error -> log.error("Error al obtener cliente {}: {}", clientId, error.getMessage()));
    }
}