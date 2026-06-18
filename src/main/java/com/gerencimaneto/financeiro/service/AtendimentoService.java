package com.gerencimaneto.financeiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gerencimaneto.financeiro.model.Atendimento;
import com.gerencimaneto.financeiro.repository.AtendimentoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository repository;

    public List<Atendimento> listarPorData(Long clienteId, LocalDate data) {
        if (data == null) {
            data = LocalDate.now();
        }
        return repository.findByClienteDonoIdAndDataAtendimentoOrderByHoraAtendimentoAsc(clienteId, data);
    }

    public List<Atendimento> listarPorSemana(Long clienteId, LocalDate dataInicio) {
        if (dataInicio == null) {
            dataInicio = LocalDate.now();
        }
        LocalDate dataFim = dataInicio.plusDays(7);
        return repository.findByClienteDonoIdAndDataAtendimentoBetweenOrderByDataAtendimentoAscHoraAtendimentoAsc(
                clienteId, dataInicio, dataFim);
    }

    public Atendimento salvarManual(Atendimento atendimento) {
        if (atendimento.getOrigem() == null || atendimento.getOrigem().isEmpty()) {
            atendimento.setOrigem("MANUAL");
        }
        return repository.save(atendimento);
    }

    public Optional<Atendimento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<Atendimento> buscarPorIdECliente(Long id, Long clienteId) {
        return repository.findByIdAndClienteDonoId(id, clienteId);
    }

    public void excluirSePertenceAoCliente(Long id, Long clienteId) {
        buscarPorIdECliente(id, clienteId).ifPresent(a -> repository.delete(a));
    }
}