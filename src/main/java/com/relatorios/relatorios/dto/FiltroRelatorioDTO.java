package com.relatorios.relatorios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroRelatorioDTO {

    private String unidade;
    private Integer ano;

    /**
     * Verifica se tem filtro de unidade
     */
    public boolean hasUnidade() {
        return unidade != null && !unidade.isEmpty() && !unidade.equals("todas");
    }

    /**
     * Verifica se tem filtro de ano
     */
    public boolean hasAno() {
        return ano != null && ano > 0;
    }

    /**
     * Verifica se tem algum filtro aplicado
     */
    public boolean hasFiltro() {
        return hasUnidade() || hasAno();
    }

    /**
     * Retorna uma descrição do filtro para usar no relatório
     */
    public String getDescricaoFiltro() {
        if (!hasFiltro()) {
            return "Todas as vendas";
        }

        StringBuilder desc = new StringBuilder();

        if (hasUnidade()) {
            desc.append("Unidade: ").append(unidade);
        }

        if (hasAno()) {
            if (desc.length() > 0) {
                desc.append(" | ");
            }
            desc.append("Ano: ").append(ano);
        }

        return desc.toString();
    }
}