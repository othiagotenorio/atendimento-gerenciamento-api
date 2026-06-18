package com.gerencimaneto.financeiro.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gerencimaneto.financeiro.model.Cliente;
import com.gerencimaneto.financeiro.model.Usuario;
import com.gerencimaneto.financeiro.repository.ClienteRepository;
import com.gerencimaneto.financeiro.repository.UsuarioRepository;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // 1. Exibe a tela de login
    @GetMapping("/login")
    public String telaLogin() {
        return "login";
    }

    /**
     * 2. Autentica em dois estágios:
     *    - 1º: verifica em tb_usuario (administradores internos) → vai para /admin
     *    - 2º: verifica em tb_cliente (clientes da plataforma)  → vai para /dashboard ou /trocar-senha
     */
    @PostMapping("/login")
    public String realizarLogin(@RequestParam("username") String email,
                                @RequestParam("password") String senha,
                                Model model,
                                HttpSession session) {

        // ── Estágio 1: Verificar em tb_usuario (Admin interno) ──
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(senha)) {
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("nomeUsuario", usuario.getNome());

            if ("ADMIN".equalsIgnoreCase(usuario.getPerfil())) {
                session.setAttribute("isAdmin", true);
                session.removeAttribute("clienteId"); // garantia: limpa sessão de cliente
                return "redirect:/admin";             // Admin vai direto para o painel
            }
        }

        // ── Estágio 2: Verificar em tb_cliente (Cliente da plataforma) ──
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        if (clienteOpt.isPresent() && clienteOpt.get().getSenha() != null
                && clienteOpt.get().getSenha().equals(senha)) {

            Cliente cliente = clienteOpt.get();

            // Verifica se o cliente está bloqueado
            if ("BLOQUEADO".equalsIgnoreCase(cliente.getStatus())) {
                model.addAttribute("erro", "Sua conta está bloqueada. Entre em contato com o administrador.");
                return "login";
            }

            // Configura a sessão do cliente
            session.setAttribute("clienteId", cliente.getId());
            session.setAttribute("clienteNome", cliente.getNomeResponsavel());
            session.setAttribute("clienteEmpresa", cliente.getNomeEmpresa());
            session.removeAttribute("isAdmin"); // garantia: limpa sessão admin

            // Primeiro acesso → troca de senha obrigatória
            if (cliente.isPrimeiroAcesso()) {
                session.setAttribute("primeiroAcesso", true);
                return "redirect:/trocar-senha";
            }

            // Acesso normal
            session.removeAttribute("primeiroAcesso");
            return "redirect:/dashboard";
        }

        // ── Credenciais inválidas ──
        model.addAttribute("erro", "E-mail ou senha inválidos!");
        return "login";
    }

    // 3. Exibe a página "Esqueci a Senha"
    @GetMapping("/esqueci-senha")
    public String exibirEsqueciSenha() {
        return "esqueci-senha";
    }

    // 4. Processa o pedido de recuperação
    @PostMapping("/esqueci-senha")
    public String processarRecuperacao(@RequestParam("email") String email, Model model) {
        boolean encontrou = usuarioRepository.findByEmail(email).isPresent()
                || clienteRepository.findByEmail(email).isPresent();

        if (encontrou) {
            model.addAttribute("sucesso", "As instruções de recuperação foram enviadas para o seu e-mail!");
        } else {
            model.addAttribute("erro", "E-mail não encontrado em nossa base de dados.");
        }

        return "esqueci-senha";
    }
}
