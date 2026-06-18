package com.gerencimaneto.financeiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gerencimaneto.financeiro.dto.AdminDashboardDTO;
import com.gerencimaneto.financeiro.dto.ClienteDetalheDTO;
import com.gerencimaneto.financeiro.model.Cliente;
import com.gerencimaneto.financeiro.repository.AtendimentoRepository;
import com.gerencimaneto.financeiro.repository.ClienteRepository;
import com.gerencimaneto.financeiro.repository.ServicoTabelaRepository;
import com.gerencimaneto.financeiro.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private ServicoTabelaRepository servicoTabelaRepository;

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente salvarCliente(Cliente cliente) {
        if (cliente.getDataCadastro() == null) {
            cliente.setDataCadastro(LocalDate.now());
        }
        if (cliente.getStatus() == null || cliente.getStatus().isBlank()) {
            cliente.setStatus("ATIVO");
        }
        // Novo cliente: garante que primeiro acesso seja obrigatório
        if (cliente.getId() == null) {
            cliente.setPrimeiroAcesso(true);
        }
        return clienteRepository.save(cliente);
    }

    public void excluirCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    public void ativarCliente(Long id) {
        clienteRepository.findById(id).ifPresent(c -> {
            c.setStatus("ATIVO");
            clienteRepository.save(c);
        });
    }

    public void bloquearCliente(Long id) {
        clienteRepository.findById(id).ifPresent(c -> {
            c.setStatus("BLOQUEADO");
            clienteRepository.save(c);
        });
    }

    // ─── Troca de Senha (Primeiro Acesso) ───────────────────

    /**
     * Atualiza a senha do cliente e marca primeiroAcesso=false,
     * liberando o acesso normal ao sistema.
     */
    public void trocarSenha(Long clienteId, String novaSenha) {
        clienteRepository.findById(clienteId).ifPresent(c -> {
            c.setSenha(novaSenha);
            c.setPrimeiroAcesso(false);
            clienteRepository.save(c);
        });
    }

    public AdminDashboardDTO obterDashboardStats() {
        long total = clienteRepository.count();
        long ativos = clienteRepository.countByStatus("ATIVO");
        long bloqueados = clienteRepository.countByStatus("BLOQUEADO");

        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate fimMes = LocalDate.now();
        long novosMes = clienteRepository.countByDataCadastroBetween(inicioMes, fimMes);

        return new AdminDashboardDTO(total, ativos, bloqueados, novosMes);
    }

    public ClienteDetalheDTO obterDetalheCliente(Long id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isEmpty()) {
            return null;
        }

        // Conta totais globais do sistema
        long qtdUsuarios = usuarioRepository.count();
        long qtdAtendimentos = atendimentoRepository.count();
        long qtdServicos = servicoTabelaRepository.count();

        return new ClienteDetalheDTO(opt.get(), qtdUsuarios, qtdAtendimentos, qtdServicos);
    }
}
