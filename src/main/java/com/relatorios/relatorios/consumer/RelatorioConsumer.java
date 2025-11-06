package com.relatorios.relatorios.consumer;

import com.relatorios.relatorios.dto.FiltroRelatorioDTO;
import com.relatorios.relatorios.dto.RelatorioMessageDTO;
import com.relatorios.relatorios.service.EmailService;
import com.relatorios.relatorios.service.RelatorioService;
import com.relatorios.relatorios.service.VendaService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Consumer que escuta a fila do RabbitMQ e processa as mensagens
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RelatorioConsumer {

    private final RelatorioService relatorioService;
    private final EmailService emailService;
    private final VendaService vendaService;

    /**
     * Escuta a fila de relatórios e processa cada mensagem
     * @param message Mensagem recebida da fila
     */
    @RabbitListener(queues = "${rabbitmq.queue.relatorios}")
    public void processarRelatorio(RelatorioMessageDTO message) {
        log.info("========================================");
        log.info("Nova mensagem recebida da fila!");
        log.info("Unidade: {}", message.getUnidade());
        log.info("Ano: {}", message.getAno());
        log.info("Email Destino: {}", message.getEmailDestino());
        log.info("Solicitado por: {}", message.getSolicitadoPor());
        log.info("========================================");

        File arquivoRelatorio = null;

        try {
            // 1. Cria o filtro
            FiltroRelatorioDTO filtro = new FiltroRelatorioDTO(
                    message.getUnidade(),
                    message.getAno()
            );

            // 2. Gera o relatório XLS
            log.info("Gerando relatório XLS...");
            arquivoRelatorio = relatorioService.gerarRelatorioXLS(filtro);
            log.info("Relatório XLS gerado: {}", arquivoRelatorio.getAbsolutePath());

            // 3. Conta quantos registros foram incluídos no relatório
            long totalRegistros = vendaService.contarVendasComFiltro(filtro);
            log.info("Total de registros no relatório: {}", totalRegistros);

            // 4. Cria o corpo do email
            String assunto = "Relatório de Vendas - " + filtro.getDescricaoFiltro();
            String corpoEmail = emailService.criarCorpoEmail(
                    filtro.getDescricaoFiltro(),
                    totalRegistros
            );

            // 5. Envia o email com o anexo
            log.info("Enviando email para: {}", message.getEmailDestino());
            emailService.enviarEmailComAnexo(
                    message.getEmailDestino(),
                    assunto,
                    corpoEmail,
                    arquivoRelatorio
            );

            log.info("========================================");
            log.info("Relatório processado e enviado com sucesso!");
            log.info("========================================");

        } catch (IOException e) {
            log.error("Erro ao gerar relatório XLS", e);
            throw new RuntimeException("Erro ao gerar relatório XLS", e);

        } catch (MessagingException e) {
            log.error("Erro ao enviar email", e);
            throw new RuntimeException("Erro ao enviar email", e);

        } finally {
            // 6. Remove o arquivo temporário após enviar
            if (arquivoRelatorio != null && arquivoRelatorio.exists()) {
                if (arquivoRelatorio.delete()) {
                    log.info("Arquivo temporário removido: {}", arquivoRelatorio.getName());
                } else {
                    log.warn("Não foi possível remover o arquivo temporário: {}", arquivoRelatorio.getName());
                }
            }
        }
    }
}