package com.relatorios.relatorios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para mensagem enviada para a fila do RabbitMQ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String unidade;
    private Integer ano;
    private String emailDestino;
    private String solicitadoPor;

    public RelatorioMessageDTO(FiltroRelatorioDTO filtro, String emailDestino, String solicitadoPor) {
        this.unidade = filtro.getUnidade();
        this.ano = filtro.getAno();
        this.emailDestino = emailDestino;
        this.solicitadoPor = solicitadoPor;
    }
}