package com.gerencimaneto.financeiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import com.gerencimaneto.financeiro.model.ServicoTabela;
import com.gerencimaneto.financeiro.service.ServicoTabelaService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/servicos-tabela")
public class ServicoTabelaController {

    @Autowired
    private ServicoTabelaService service;

    /**
     * Retorna todos os serviços padrão como JSON para o JS da tela de atendimentos
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<ServicoTabela>> listarServicos(HttpSession session) {
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.listarTodos(clienteId));
    }

    /**
     * Salva ou atualiza um serviço padrão (upsert por tag)
     */
    @PostMapping("/salvar")
    @ResponseBody
    public ResponseEntity<ServicoTabela> salvarServico(
            HttpSession session,
            @RequestParam("tag") String tag,
            @RequestParam("valor") BigDecimal valor) {

        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) return ResponseEntity.status(401).build();

        if (tag == null || tag.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ServicoTabela salvo = service.salvar(clienteId, tag, valor);
        return ResponseEntity.ok(salvo);
    }

    /**
     * Remove um serviço padrão pelo ID
     */
    @PostMapping("/deletar/{id}")
    @ResponseBody
    public ResponseEntity<Void> deletarServico(HttpSession session, @PathVariable("id") Long id) {
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId != null) {
            service.deletar(clienteId, id);
        }
        return ResponseEntity.ok().build();
    }
}
