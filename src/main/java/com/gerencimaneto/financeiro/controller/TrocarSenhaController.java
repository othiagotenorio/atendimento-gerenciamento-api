package com.gerencimaneto.financeiro.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gerencimaneto.financeiro.service.AdminService;

@Controller
@RequestMapping("/trocar-senha")
public class TrocarSenhaController {

    @Autowired
    private AdminService adminService;

    /**
     * Exibe a tela de troca de senha obrigatória no primeiro acesso.
     * Apenas clientes com primeiroAcesso=true na sessão podem acessar.
     */
    @GetMapping
    public String exibirTrocaSenha(HttpSession session, Model model) {
        Long clienteId = (Long) session.getAttribute("clienteId");
        Boolean primeiroAcesso = (Boolean) session.getAttribute("primeiroAcesso");

        // Proteção: somente clientes logados com primeiroAcesso pendente
        if (clienteId == null || !Boolean.TRUE.equals(primeiroAcesso)) {
            return "redirect:/login";
        }

        String clienteNome = (String) session.getAttribute("clienteNome");
        model.addAttribute("clienteNome", clienteNome);
        return "trocar-senha";
    }

    /**
     * Processa a nova senha:
     *  - Valida que novaSenha == confirmacao
     *  - Atualiza no banco via AdminService
     *  - Remove flag de primeiroAcesso da sessão
     *  - Redireciona para o dashboard
     */
    @PostMapping
    public String processarTrocaSenha(@RequestParam("novaSenha") String novaSenha,
                                      @RequestParam("confirmacao") String confirmacao,
                                      HttpSession session,
                                      Model model) {

        Long clienteId = (Long) session.getAttribute("clienteId");
        Boolean primeiroAcesso = (Boolean) session.getAttribute("primeiroAcesso");

        if (clienteId == null || !Boolean.TRUE.equals(primeiroAcesso)) {
            return "redirect:/login";
        }

        // Validação: senha não pode ser vazia
        if (novaSenha == null || novaSenha.isBlank()) {
            model.addAttribute("erro", "A nova senha não pode ser vazia.");
            return "trocar-senha";
        }

        // Validação: senhas devem coincidir
        if (!novaSenha.equals(confirmacao)) {
            model.addAttribute("erro", "As senhas não coincidem. Tente novamente.");
            return "trocar-senha";
        }

        // Validação: mínimo de 6 caracteres
        if (novaSenha.length() < 6) {
            model.addAttribute("erro", "A senha deve ter pelo menos 6 caracteres.");
            return "trocar-senha";
        }

        // Atualiza no banco e libera o acesso
        adminService.trocarSenha(clienteId, novaSenha);

        // Remove a flag de primeiro acesso da sessão
        session.removeAttribute("primeiroAcesso");

        return "redirect:/dashboard";
    }
}
