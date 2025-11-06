package com.relatorios.relatorios.service;

import com.relatorios.relatorios.dto.RelatorioMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.relatorios}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    /**
     * Envia mensagem para a fila do RabbitMQ
     * @param message Mensagem com os dados do relatório
     */
    public void enviarMensagemRelatorio(RelatorioMessageDTO message) {
        log.info("Enviando mensagem para a fila: {}", message);

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.info("Mensagem enviada com sucesso para a fila!");
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para a fila", e);
            throw new RuntimeException("Erro ao enviar mensagem para a fila", e);
        }
    }
}