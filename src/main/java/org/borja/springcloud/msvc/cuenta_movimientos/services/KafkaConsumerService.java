package org.borja.springcloud.msvc.cuenta_movimientos.services;

import org.borja.springcloud.msvc.cuenta_movimientos.event.ClienteEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final KafkaTemplate<String, ClienteEvent> kafkaTemplate;

    @KafkaListener(topics = "${kafka.topic.cliente}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeClienteEvent(ClienteEvent event) {
        System.out.println("Consumed event: " + event);
    }

    public void sendClienteEvent(ClienteEvent event) {
        kafkaTemplate.send(clienteTopic, event);
    }
}
