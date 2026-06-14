package com.gerencimaneto.financeiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gerencimaneto.financeiro.model.ServicoTabela;
import com.gerencimaneto.financeiro.repository.ServicoTabelaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ServicoTabelaService {

    @Autowired
    private ServicoTabelaRepository repository;

    public List<ServicoTabela> listarTodos() {
        return repository.findAllByOrderByTagAsc();
    }

    public ServicoTabela salvar(String tag, BigDecimal valor) {
        // Se já existe uma tag igual, atualiza o valor
        Optional<ServicoTabela> existente = repository.findByTagIgnoreCase(tag);
        ServicoTabela servico = existente.orElse(new ServicoTabela());
        servico.setTag(tag);
        servico.setValor(valor);
        return repository.save(servico);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Optional<BigDecimal> buscarValorPorTag(String tag) {
        return repository.findByTagIgnoreCase(tag).map(ServicoTabela::getValor);
    }
}
