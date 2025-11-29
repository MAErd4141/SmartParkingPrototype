package com.akilliotopark.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Kuyruk İsimleri
    public static final String QUEUE_IOT = "parking.iot.queue";     // Sensörler buraya atacak
    public static final String QUEUE_CAMERA = "parking.camera.queue"; // Flask (Kamera) buraya atacak
    public static final String EXCHANGE = "parking.events";         // Dağıtım merkezi

    // 1. IoT Kuyruğu
    @Bean
    public Queue iotQueue() {
        return new Queue(QUEUE_IOT, true); // true = sunucu kapansa da kuyruk silinmez (Durable)
    }

    // 2. Kamera Kuyruğu
    @Bean
    public Queue cameraQueue() {
        return new Queue(QUEUE_CAMERA, true);
    }

    // 3. Exchange (Postane Merkezi)
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // 4. Bağlantılar (Binding)
    // "parking.sensor" ile başlayan mesajlar -> iotQueue'ya gitsin
    @Bean
    public Binding iotBinding(Queue iotQueue, TopicExchange exchange) {
        return BindingBuilder.bind(iotQueue).to(exchange).with("parking.sensor.#");
    }

    // "parking.camera" ile başlayan mesajlar -> cameraQueue'ya gitsin
    @Bean
    public Binding cameraBinding(Queue cameraQueue, TopicExchange exchange) {
        return BindingBuilder.bind(cameraQueue).to(exchange).with("parking.camera.#");
    }

    // 5. JSON Dönüştürücü (Mesajları String değil JSON olarak okumak için)
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}