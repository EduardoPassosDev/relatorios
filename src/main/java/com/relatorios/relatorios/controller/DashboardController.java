package com.relatorios.relatorios.controller;


import com.relatorios.relatorios.dto.FiltroRelatorioDTO;
import com.relatorios.relatorios.dto.RelatorioMessageDTO;
import com.relatorios.relatorios.dto.VendaDTO;
import com.relatorios.relatorios.model.Usuario;
import com.relatorios.relatorios.model.Venda;
import com.relatorios.relatorios.security.CustomUserDetailsService;
import com.relatorios.relatorios.service.RabbitMQService;
import com.relatorios.relatorios.service.VendaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsável pelo dashboard e geração de relatórios
 */
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final VendaService vendaService;
    private final RabbitMQService rabbitMQService;
    private final CustomUserDetailsService userDetailsService;

    @Value("${mail.to.miguel}")
    private String emailMiguel;

    /**
     * Exibe o dashboard com as vendas filtradas
     */
    @GetMapping
    public String dashboard(
            @RequestParam(value = "unidade", required = false) String unidade,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Authentication authentication,
            Model model) {

        log.info("Acessando dashboard - Unidade: {}, Ano: {}, Page: {}", unidade, ano, page);

        // Busca informações do usuário logado
        String email = authentication.getName();
        Usuario usuario = userDetailsService.findUsuarioByEmail(email);
        model.addAttribute("usuario", usuario);

        // Cria o filtro
        FiltroRelatorioDTO filtro = new FiltroRelatorioDTO(unidade, ano);

        // Busca vendas com paginação (8 registros por página)
        Page<Venda> vendasPage = vendaService.buscarVendasComFiltro(filtro, page, 8);

        // Converte para DTO
        List<VendaDTO> vendas = vendasPage.getContent().stream()
                .map(VendaDTO::fromEntity)
                .collect(Collectors.toList());

        // Busca unidades e anos para os filtros
        List<String> unidades = vendaService.buscarUnidadesDistintas();
        List<Integer> anos = vendaService.buscarAnosDistintos();

        // Total de vendas com o filtro
        long totalVendas = vendaService.contarVendasComFiltro(filtro);

        // Adiciona atributos ao model
        model.addAttribute("vendas", vendas);
        model.addAttribute("unidades", unidades);
        model.addAttribute("anos", anos);
        model.addAttribute("filtro", filtro);
        model.addAttribute("totalVendas", totalVendas);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vendasPage.getTotalPages());
        model.addAttribute("hasNext", vendasPage.hasNext());
        model.addAttribute("hasPrevious", vendasPage.hasPrevious());

        log.info("Dashboard carregado - Total de vendas: {}, Página: {}/{}",
                totalVendas, page + 1, vendasPage.getTotalPages());

        return "dashboard";
    }

    /**
     * Gera relatório e envia para a fila do RabbitMQ
     */
    @PostMapping("/gerar-relatorio")
    public String gerarRelatorio(
            @RequestParam(value = "unidade", required = false) String unidade,
            @RequestParam(value = "ano", required = false) Integer ano,
            Authentication authentication,
            Model model) {

        log.info("Solicitação de relatório - Unidade: {}, Ano: {}", unidade, ano);

        try {
            // Busca informações do usuário logado
            String email = authentication.getName();
            Usuario usuario = userDetailsService.findUsuarioByEmail(email);

            // Cria o filtro
            FiltroRelatorioDTO filtro = new FiltroRelatorioDTO(unidade, ano);

            // Cria a mensagem para enviar para a fila
            RelatorioMessageDTO message = new RelatorioMessageDTO(
                    filtro,
                    emailMiguel,
                    usuario.getNome()
            );

            // Envia para a fila do RabbitMQ
            rabbitMQService.enviarMensagemRelatorio(message);

            log.info("Relatório enviado para a fila com sucesso!");

            // Adiciona atributos ao model
            model.addAttribute("usuario", usuario);
            model.addAttribute("filtro", filtro);
            model.addAttribute("emailDestino", emailMiguel);

            return "relatorio-enviado";

        } catch (Exception e) {
            log.error("Erro ao gerar relatório", e);
            model.addAttribute("errorMessage", "Erro ao gerar relatório: " + e.getMessage());
            return "redirect:/dashboard?error=true";
        }
    }
}