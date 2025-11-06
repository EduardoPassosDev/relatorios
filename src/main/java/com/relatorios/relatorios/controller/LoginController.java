package com.relatorios.relatorios.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller responsável pela página de login
 */
@Controller
@Slf4j
public class LoginController {

    /**
     * Exibe a página de login
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        log.debug("Acessando página de login");

        if (error != null) {
            model.addAttribute("errorMessage", "Email ou senha inválidos!");
            log.debug("Tentativa de login falhou");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Logout realizado com sucesso!");
            log.debug("Usuário fez logout");
        }

        return "login";
    }

    /**
     * Redireciona a raiz para o dashboard (se autenticado) ou login
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
}