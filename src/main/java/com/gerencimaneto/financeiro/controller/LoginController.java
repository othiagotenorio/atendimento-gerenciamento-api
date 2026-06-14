package com.gerencimaneto.financeiro.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.gerencimaneto.financeiro.model.Usuario;
import com.gerencimaneto.financeiro.repository.UsuarioRepository;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Exibe a tela de login
    @GetMapping("/login")
    public String telaLogin() {
        return "login";
    }

    // 2. CORREÇÃO: Recebe os dados de login (POST) e valida manualmente
    @PostMapping("/login")
    public String realizarLoginManual(@RequestParam("username") String email,
            @RequestParam("password") String senha,
            Model model) {

        // Busca o usuário pelo e-mail digitado
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        // Se o usuário existir e a senha for igual à do banco...
        if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(senha)) {
            // Login efetuado com sucesso! Redireciona para o Dashboard (raiz)
            return "redirect:/dashboard";
        }

        // Se der errado, joga de volta pro login com uma mensagem de erro
        model.addAttribute("erro", "E-mail ou senha inválidos!");
        return "login";
    }

    // Exibe a página "Esqueci a Senha"
    @GetMapping("/esqueci-senha")
    public String exibirEsqueciSenha() {
        return "esqueci-senha";
    }

    // Processa o pedido de recuperação
    @PostMapping("/esqueci-senha")
    public String processarRecuperacao(@RequestParam("email") String email, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            // Aqui no futuro entra a lógica de enviar o e-mail real
            model.addAttribute("sucesso", "As instruções de recuperação foram enviadas para o seu e-mail!");
        } else {
            model.addAttribute("erro", "E-mail não encontrado em nossa base de dados.");
        }

        return "esqueci-senha";
    }
}
