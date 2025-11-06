package com.relatorios.relatorios.dto;

import com.relatorios.relatorios.model.Venda;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO para exibir vendas na view
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaDTO {

    private Long id;
    private String vendedor;
    private String cliente;
    private String unidade;
    private BigDecimal valorVenda;
    private LocalDateTime dataVenda;

    /**
     * Converte uma entidade Venda para DTO
     */
    public static VendaDTO fromEntity(Venda venda) {
        return new VendaDTO(
                venda.getId(),
                venda.getVendedor(),
                venda.getCliente(),
                venda.getUnidade(),
                venda.getValorVenda(),
                venda.getDataVenda()
        );
    }

    /**
     * Retorna a data formatada para exibição
     */
    public String getDataVendaFormatada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dataVenda.format(formatter);
    }

    /**
     * Retorna o valor formatado como moeda brasileira
     */
    public String getValorVendaFormatado() {
        return String.format("R$ %.2f", valorVenda);
    }

    /**
     * Retorna o ano da venda
     */
    public int getAno() {
        return dataVenda.getYear();
    }
}