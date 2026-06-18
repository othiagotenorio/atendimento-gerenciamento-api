package com.gerencimaneto.financeiro.controller;

import com.gerencimaneto.financeiro.model.Profissional;
import com.gerencimaneto.financeiro.repository.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalRepository repository;

    @PostMapping("/salvar")
    public String salvarProfissional(@RequestParam("nome") String nome, 
                                     @RequestParam(value = "dataBusca", required = false) String dataBusca) {
        if (nome != null && !nome.isBlank()) {
            Profissional prof = new Profissional(nome.trim());
            repository.save(prof);
        }
        return "redirect:/atendimentos" + (dataBusca != null && !dataBusca.isBlank() ? "?dataFiltro=" + dataBusca : "");
    }

    @GetMapping("/deletar/{id}")
    public String deletarProfissional(@PathVariable("id") Long id,
                                      @RequestParam(value = "dataBusca", required = false) String dataBusca) {
        repository.deleteById(id);
        return "redirect:/atendimentos" + (dataBusca != null && !dataBusca.isBlank() ? "?dataFiltro=" + dataBusca : "");
    }
}
