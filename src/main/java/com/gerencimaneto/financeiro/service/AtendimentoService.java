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

    // Listar filtrando direto pela LocalDate escolhida
    public List<Atendimento> listarPorData(LocalDate data) {
        if (data == null) {
            data = LocalDate.now(); // Se não informou data, assume a data de hoje
        }
        return repository.findByDataAtendimentoOrderByHoraAtendimentoAsc(data);
    }

    // Listar atendimentos no intervalo de uma semana usando LocalDate
    public List<Atendimento> listarPorSemana(LocalDate dataInicio) {
        if (dataInicio == null) {
            dataInicio = LocalDate.now();
        }
        LocalDate dataFim = dataInicio.plusDays(7);
        return repository.findByDataAtendimentoBetweenOrderByDataAtendimentoAscHoraAtendimentoAsc(dataInicio, dataFim);
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

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}