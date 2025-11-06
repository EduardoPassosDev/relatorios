package com.relatorios.relatorios.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.relatorios}")
    private String queueName;

    @Value("${rabbitmq.exchange.relatorios}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    /**
     * Cria a fila de relatórios
     */
    @Bean
    public Queue relatoriosQueue() {
        return new Queue(queueName, true); // true = durável (persiste após reiniciar o RabbitMQ)
    }

    /**
     * Cria o exchange (tipo Topic)
     */
    @Bean
    public TopicExchange relatoriosExchange() {
        return new TopicExchange(exchangeName);
    }

    /**
     * Faz o binding da fila com o exchange usando a routing key
     */
    @Bean
    public Binding binding(Queue relatoriosQueue, TopicExchange relatoriosExchange) {
        return BindingBuilder
                .bind(relatoriosQueue)
                .to(relatoriosExchange)
                .with(routingKey);
    }

    /**
     * Conversor de mensagens para JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Template do RabbitMQ com conversor JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}