package com.gerencimaneto.financeiro.controller;

import com.gerencimaneto.financeiro.model.Profissional;
import com.gerencimaneto.financeiro.repository.ProfissionalRepository;
import com.gerencimaneto.financeiro.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/salvar")
    public String salvarProfissional(
            HttpSession session,
            @RequestParam("nome") String nome, 
            @RequestParam(value = "dataBusca", required = false) String dataBusca) {
        
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId != null && nome != null && !nome.isBlank()) {
            Profissional prof = new Profissional(nome.trim());
            clienteRepository.findById(clienteId).ifPresent(prof::setClienteDono);
            repository.save(prof);
        }
        return "redirect:/atendimentos" + (dataBusca != null && !dataBusca.isBlank() ? "?dataFiltro=" + dataBusca : "");
    }

    @GetMapping("/deletar/{id}")
    public String deletarProfissional(
            HttpSession session,
            @PathVariable("id") Long id,
            @RequestParam(value = "dataBusca", required = false) String dataBusca) {
        
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId != null) {
            repository.findByIdAndClienteDonoId(id, clienteId).ifPresent(repository::delete);
        }
        return "redirect:/atendimentos" + (dataBusca != null && !dataBusca.isBlank() ? "?dataFiltro=" + dataBusca : "");
    }
}
