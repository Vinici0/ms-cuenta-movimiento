package org.borja.springcloud.msvc.account.services.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.borja.springcloud.msvc.account.dtos.client.ClientResponseDto;
import org.borja.springcloud.msvc.account.exceptions.ResourceNotFoundException;
import org.borja.springcloud.msvc.account.response.ApiResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WebClientService {
    private static final Logger log = LoggerFactory.getLogger(WebClientService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public WebClientService(@Value("${microservice.clients.url}") String clientsServiceUrl, ObjectMapper objectMapper) {
        this.webClient = WebClient.create(clientsServiceUrl);
        this.objectMapper = objectMapper;
    }

    public Mono<ClientResponseDto> findClientById(Long clientId) {
        log.info("Calling clients service for client ID: {}", clientId);
        return webClient
                .get()
                .uri("/api/clientes/{id}", clientId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new ResourceNotFoundException(
                                "Error calling client service for ID: " + clientId)))
                .bodyToMono(ApiResponse.class)
                .flatMap(response -> {
                    if (response.getData() == null) {
                        return Mono.error(new ResourceNotFoundException(
                                "Client not found with ID: " + clientId));
                    }
                    try {
                        ClientResponseDto client = objectMapper.convertValue(
                                response.getData(),
                                ClientResponseDto.class
                        );
                        return Mono.just(client);
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new ResourceNotFoundException(
                                "Error mapping client data: " + e.getMessage()));
                    }
                })
                .doOnError(error -> log.error("Error fetching client {}: {}",
                        clientId, error.getMessage()));
    }
}