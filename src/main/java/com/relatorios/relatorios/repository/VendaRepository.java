package com.relatorios.relatorios.repository;

import com.relatorios.relatorios.model.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    /**
     * Busca vendas por unidade e ano com paginação
     * @param unidade Nome da unidade
     * @param ano Ano da venda
     * @param pageable Configuração de paginação
     * @return Página de vendas
     */
    @Query("SELECT v FROM Venda v WHERE v.unidade = :unidade AND YEAR(v.dataVenda) = :ano ORDER BY v.dataVenda DESC")
    Page<Venda> findByUnidadeAndAno(
            @Param("unidade") String unidade,
            @Param("ano") int ano,
            Pageable pageable
    );

    /**
     * Busca vendas por unidade com paginação
     * @param unidade Nome da unidade
     * @param pageable Configuração de paginação
     * @return Página de vendas
     */
    Page<Venda> findByUnidadeOrderByDataVendaDesc(String unidade, Pageable pageable);

    /**
     * Busca vendas por ano com paginação
     * @param ano Ano da venda
     * @param pageable Configuração de paginação
     * @return Página de vendas
     */
    @Query("SELECT v FROM Venda v WHERE YEAR(v.dataVenda) = :ano ORDER BY v.dataVenda DESC")
    Page<Venda> findByAno(@Param("ano") int ano, Pageable pageable);

    /**
     * Busca todas as vendas com paginação
     * @param pageable Configuração de paginação
     * @return Página de vendas
     */
    Page<Venda> findAllByOrderByDataVendaDesc(Pageable pageable);

    /**
     * Busca todas as vendas por unidade e ano (sem paginação) para gerar o relatório completo
     * @param unidade Nome da unidade
     * @param ano Ano da venda
     * @return Lista de vendas
     */
    @Query("SELECT v FROM Venda v WHERE v.unidade = :unidade AND YEAR(v.dataVenda) = :ano ORDER BY v.dataVenda DESC")
    List<Venda> findAllByUnidadeAndAno(
            @Param("unidade") String unidade,
            @Param("ano") int ano
    );

    /**
     * Busca todas as vendas por unidade (sem paginação)
     * @param unidade Nome da unidade
     * @return Lista de vendas
     */
    List<Venda> findAllByUnidadeOrderByDataVendaDesc(String unidade);

    /**
     * Busca todas as vendas por ano (sem paginação)
     * @param ano Ano da venda
     * @return Lista de vendas
     */
    @Query("SELECT v FROM Venda v WHERE YEAR(v.dataVenda) = :ano ORDER BY v.dataVenda DESC")
    List<Venda> findAllByAno(@Param("ano") int ano);

    /**
     * Busca todas as vendas (sem paginação)
     * @return Lista de vendas
     */
    List<Venda> findAllByOrderByDataVendaDesc();

    /**
     * Busca unidades distintas
     * @return Lista de unidades
     */
    @Query("SELECT DISTINCT v.unidade FROM Venda v ORDER BY v.unidade")
    List<String> findDistinctUnidades();

    /**
     * Busca anos distintos
     * @return Lista de anos
     */
    @Query("SELECT DISTINCT YEAR(v.dataVenda) FROM Venda v ORDER BY YEAR(v.dataVenda) DESC")
    List<Integer> findDistinctAnos();
}