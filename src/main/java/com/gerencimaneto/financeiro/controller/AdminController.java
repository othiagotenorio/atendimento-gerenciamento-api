package com.gerencimaneto.financeiro.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gerencimaneto.financeiro.dto.AdminDashboardDTO;
import com.gerencimaneto.financeiro.dto.ClienteDetalheDTO;
import com.gerencimaneto.financeiro.model.Cliente;
import com.gerencimaneto.financeiro.service.AdminService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Verificação de acesso ADMIN (usado em todos os endpoints)
    private boolean isAdmin(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("isAdmin"));
    }

    // ========================
    // DASHBOARD
    // ========================

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        AdminDashboardDTO stats = adminService.obterDashboardStats();
        List<Cliente> clientes = adminService.listarTodosClientes();

        model.addAttribute("stats", stats);
        model.addAttribute("clientes", clientes);
        return "admin";
    }

    // ========================
    // LISTAGEM DE CLIENTES
    // ========================

    @GetMapping("/clientes")
    public String listarClientes(HttpSession session, Model model,
                                 @RequestParam(value = "busca", required = false) String busca) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<Cliente> clientes = adminService.listarTodosClientes();
        model.addAttribute("clientes", clientes);
        return "admin-clientes";
    }

    // ========================
    // NOVO CLIENTE
    // ========================

    @GetMapping("/clientes/novo")
    public String formNovoCliente(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("modoEdicao", false);
        return "admin-cliente-form";
    }

    @PostMapping("/clientes/novo")
    public String salvarNovoCliente(HttpSession session,
                                    @ModelAttribute("cliente") Cliente cliente,
                                    RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            adminService.salvarCliente(cliente);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente cadastrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cadastrar cliente: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }

    // ========================
    // DETALHES DO CLIENTE
    // ========================

    @GetMapping("/clientes/{id}")
    public String detalheCliente(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        ClienteDetalheDTO detalhe = adminService.obterDetalheCliente(id);
        if (detalhe == null) {
            return "redirect:/admin/clientes";
        }

        model.addAttribute("detalhe", detalhe);
        return "admin-cliente-detalhe";
    }

    // ========================
    // EDITAR CLIENTE
    // ========================

    @GetMapping("/clientes/{id}/editar")
    public String formEditarCliente(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Optional<Cliente> opt = adminService.buscarClientePorId(id);
        if (opt.isEmpty()) {
            return "redirect:/admin/clientes";
        }

        model.addAttribute("cliente", opt.get());
        model.addAttribute("modoEdicao", true);
        return "admin-cliente-form";
    }

    @PostMapping("/clientes/{id}/editar")
    public String salvarEdicaoCliente(@PathVariable Long id,
                                      HttpSession session,
                                      @ModelAttribute("cliente") Cliente clienteAtualizado,
                                      RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Optional<Cliente> opt = adminService.buscarClientePorId(id);
        if (opt.isPresent()) {
            Cliente existente = opt.get();
            existente.setNomeEmpresa(clienteAtualizado.getNomeEmpresa());
            existente.setNomeResponsavel(clienteAtualizado.getNomeResponsavel());
            existente.setEmail(clienteAtualizado.getEmail());
            existente.setTelefone(clienteAtualizado.getTelefone());
            existente.setPlano(clienteAtualizado.getPlano());
            existente.setStatus(clienteAtualizado.getStatus());
            existente.setCpf(clienteAtualizado.getCpf());

            // Atualiza a senha somente se o admin informou uma nova (campo opcional na edição)
            if (clienteAtualizado.getSenha() != null && !clienteAtualizado.getSenha().isBlank()) {
                existente.setSenha(clienteAtualizado.getSenha());
                existente.setPrimeiroAcesso(true); // força troca de senha no próximo login
            }

            adminService.salvarCliente(existente);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente atualizado com sucesso!");
        }

        return "redirect:/admin/clientes";
    }

    // ========================
    // ATIVAR / BLOQUEAR
    // ========================

    @GetMapping("/clientes/{id}/ativar")
    public String ativarCliente(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        adminService.ativarCliente(id);
        redirectAttributes.addFlashAttribute("sucesso", "Cliente ativado com sucesso!");
        return "redirect:/admin/clientes";
    }

    @GetMapping("/clientes/{id}/bloquear")
    public String bloquearCliente(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        adminService.bloquearCliente(id);
        redirectAttributes.addFlashAttribute("aviso", "Cliente bloqueado.");
        return "redirect:/admin/clientes";
    }

    // ========================
    // EXCLUIR
    // ========================

    @GetMapping("/clientes/{id}/excluir")
    public String excluirCliente(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        adminService.excluirCliente(id);
        redirectAttributes.addFlashAttribute("sucesso", "Cliente excluído com sucesso!");
        return "redirect:/admin/clientes";
    }
}
