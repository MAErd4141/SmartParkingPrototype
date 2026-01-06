package com.akilliotopark.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_IOT = "parking.iot.queue";
    public static final String QUEUE_CAMERA = "parking.camera.queue";
    public static final String EXCHANGE = "parking.events";

    @Bean
    public Queue iotQueue() {
        return new Queue(QUEUE_IOT, true);
    }

    @Bean
    public Queue cameraQueue() {
        return new Queue(QUEUE_CAMERA, true);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding iotBinding(Queue iotQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(iotQueue).to(topicExchange).with("parking.sensor.#");
    }

    @Bean
    public Binding cameraBinding(Queue cameraQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(cameraQueue).to(topicExchange).with("parking.camera.#");
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @Primary
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}