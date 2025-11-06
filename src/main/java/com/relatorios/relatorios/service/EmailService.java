package com.relatorios.relatorios.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String mailFrom;

    /**
     * Envia email com o relatório anexado
     * @param destinatario Email do destinatário
     * @param assunto Assunto do email
     * @param mensagem Corpo do email
     * @param anexo Arquivo a ser anexado
     * @throws MessagingException Se houver erro ao enviar o email
     */
    public void enviarEmailComAnexo(String destinatario, String assunto, String mensagem, File anexo)
            throws MessagingException {

        log.info("Preparando email para: {}", destinatario);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(mailFrom);
        helper.setTo(destinatario);
        helper.setSubject(assunto);
        helper.setText(mensagem, true); // true = HTML

        // Anexa o arquivo
        if (anexo != null && anexo.exists()) {
            FileSystemResource file = new FileSystemResource(anexo);
            helper.addAttachment(anexo.getName(), file);
            log.info("Arquivo anexado: {}", anexo.getName());
        }

        mailSender.send(mimeMessage);
        log.info("Email enviado com sucesso para: {}", destinatario);
    }

    /**
     * Cria o corpo HTML do email
     * @param filtroDescricao Descrição do filtro aplicado
     * @param totalRegistros Total de registros no relatório
     * @return HTML do email
     */
    public String criarCorpoEmail(String filtroDescricao, long totalRegistros) {
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 20px; border: 1px solid #ddd; border-radius: 0 0 5px 5px; }
                    .info { background-color: #fff; padding: 15px; margin: 10px 0; border-left: 4px solid #4CAF50; }
                    .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }
                    .highlight { color: #4CAF50; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📊 Relatório de Vendas</h1>
                    </div>
                    <div class="content">
                        <p>Olá,</p>
                        <p>Seu relatório de vendas foi gerado com sucesso!</p>
                        
                        <div class="info">
                            <p><strong>Filtro Aplicado:</strong> %s</p>
                            <p><strong>Total de Registros:</strong> <span class="highlight">%d</span></p>
                            <p><strong>Data/Hora:</strong> %s</p>
                        </div>
                        
                        <p>O arquivo Excel está anexado a este email.</p>
                        
                        <p>Atenciosamente,<br>
                        <strong>Sistema de Relatórios</strong></p>
                    </div>
                    <div class="footer">
                        <p>Este é um email automático. Por favor, não responda.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(filtroDescricao, totalRegistros, dataHora);
    }
}