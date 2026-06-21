package com.gerencimaneto.financeiro.controller;

import com.gerencimaneto.financeiro.model.SolicitacaoCadastro;
import com.gerencimaneto.financeiro.repository.SolicitacaoCadastroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CadastroController {

    @Autowired
    private SolicitacaoCadastroRepository solicitacaoRepository;

    @GetMapping("/cadastro")
    public String exibirFormulario(
            @RequestParam(value = "enviado", required = false) String enviado,
            Model model) {
        model.addAttribute("enviado", enviado != null);
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String enviarSolicitacao(
            @RequestParam("nomeResponsavel") String nomeResponsavel,
            @RequestParam("nomeEstabelecimento") String nomeEstabelecimento,
            @RequestParam("cpfCnpj") String cpfCnpj,
            @RequestParam("email") String email,
            @RequestParam("telefone") String telefone,
            RedirectAttributes redirectAttributes) {

        // Verifica se já existe uma solicitação com esse e-mail ainda não rejeitada
        boolean jaExiste = solicitacaoRepository.findAllByOrderByDataSolicitacaoDesc()
                .stream()
                .anyMatch(s -> s.getEmail().equalsIgnoreCase(email.trim())
                        && s.getStatus() != SolicitacaoCadastro.Status.REJEITADO);

        if (jaExiste) {
            redirectAttributes.addFlashAttribute("erro",
                    "Já existe uma solicitação cadastrada com este e-mail. Nossa equipe entrará em contato em breve.");
            return "redirect:/cadastro";
        }

        SolicitacaoCadastro sol = new SolicitacaoCadastro();
        sol.setNomeResponsavel(nomeResponsavel.trim());
        sol.setNomeEstabelecimento(nomeEstabelecimento.trim());
        sol.setCpfCnpj(cpfCnpj.trim());
        sol.setEmail(email.trim().toLowerCase());
        sol.setTelefone(telefone.trim());
        solicitacaoRepository.save(sol);

        return "redirect:/cadastro?enviado=true";
    }
}