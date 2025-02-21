package org.borja.springcloud.msvc.cuenta_movimientos.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.borja.springcloud.msvc.cuenta_movimientos.event.ClienteEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, ClienteEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "msvc-cuentas-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // Permitir cualquier paquete
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        // Indicar el tipo por defecto (la clase local)
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.borja.springcloud.msvc.cuenta_movimientos.event.ClienteEvent");
        // Dar prioridad al payload y no a los headers
        props.put("spring.json.type.precedence", "payload");
        // Indicar que NO se use la información de tipo del header
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(ClienteEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClienteEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ClienteEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
