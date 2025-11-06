package com.relatorios.relatorios.service;


import com.relatorios.relatorios.dto.FiltroRelatorioDTO;
import com.relatorios.relatorios.model.Venda;
import com.relatorios.relatorios.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VendaService {

    private final VendaRepository vendaRepository;

    /**
     * Busca vendas com filtro e paginação (para exibir na tela)
     * @param filtro Filtro de unidade e ano
     * @param page Número da página
     * @param size Tamanho da página
     * @return Página de vendas
     */
    public Page<Venda> buscarVendasComFiltro(FiltroRelatorioDTO filtro, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        log.debug("Buscando vendas com filtro: {}", filtro);

        // Aplica os filtros conforme necessário
        if (filtro.hasUnidade() && filtro.hasAno()) {
            return vendaRepository.findByUnidadeAndAno(filtro.getUnidade(), filtro.getAno(), pageable);
        } else if (filtro.hasUnidade()) {
            return vendaRepository.findByUnidadeOrderByDataVendaDesc(filtro.getUnidade(), pageable);
        } else if (filtro.hasAno()) {
            return vendaRepository.findByAno(filtro.getAno(), pageable);
        } else {
            return vendaRepository.findAllByOrderByDataVendaDesc(pageable);
        }
    }

    /**
     * Busca TODAS as vendas com filtro (SEM paginação) para gerar o relatório completo
     * @param filtro Filtro de unidade e ano
     * @return Lista completa de vendas
     */
    public List<Venda> buscarTodasVendasComFiltro(FiltroRelatorioDTO filtro) {
        log.debug("Buscando TODAS as vendas com filtro: {}", filtro);

        // Aplica os filtros conforme necessário
        if (filtro.hasUnidade() && filtro.hasAno()) {
            return vendaRepository.findAllByUnidadeAndAno(filtro.getUnidade(), filtro.getAno());
        } else if (filtro.hasUnidade()) {
            return vendaRepository.findAllByUnidadeOrderByDataVendaDesc(filtro.getUnidade());
        } else if (filtro.hasAno()) {
            return vendaRepository.findAllByAno(filtro.getAno());
        } else {
            return vendaRepository.findAllByOrderByDataVendaDesc();
        }
    }

    /**
     * Busca todas as unidades distintas
     * @return Lista de unidades
     */
    public List<String> buscarUnidadesDistintas() {
        return vendaRepository.findDistinctUnidades();
    }

    /**
     * Busca todos os anos distintos
     * @return Lista de anos
     */
    public List<Integer> buscarAnosDistintos() {
        return vendaRepository.findDistinctAnos();
    }

    /**
     * Conta total de vendas com filtro
     * @param filtro Filtro de unidade e ano
     * @return Quantidade de vendas
     */
    public long contarVendasComFiltro(FiltroRelatorioDTO filtro) {
        return buscarTodasVendasComFiltro(filtro).size();
    }
}