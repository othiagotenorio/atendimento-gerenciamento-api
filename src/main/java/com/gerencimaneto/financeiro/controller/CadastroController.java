package com.gerencimaneto.financeiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gerencimaneto.financeiro.dto.UsuarioCadastroDTO;
import com.gerencimaneto.financeiro.service.CadastroService;

@Controller
public class CadastroController {

    @Autowired
    private CadastroService cadastroService;

    @GetMapping("/cadastro")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioCadastroDTO());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrarUsuario(@ModelAttribute("usuarioDTO") UsuarioCadastroDTO dto, Model model) {
        try {
            // O Controller apenas chama o Service e passa o DTO pra frente
            cadastroService.cadastrarNovoUsuario(dto);

            // Sucesso: Redireciona para o login
            return "redirect:/login?sucessoCadastro";

        } catch (IllegalArgumentException e) {
            // Se o Service barrar o e-mail, capturamos a mensagem e mandamos de volta pro
            // HTML
            model.addAttribute("erro", e.getMessage());
            return "cadastro";
        }
    }
}