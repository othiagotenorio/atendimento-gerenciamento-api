package com.gerencimaneto.financeiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gerencimaneto.financeiro.model.ServicoTabela;
import com.gerencimaneto.financeiro.repository.ServicoTabelaRepository;
import com.gerencimaneto.financeiro.repository.ClienteRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ServicoTabelaService {

    @Autowired
    private ServicoTabelaRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<ServicoTabela> listarTodos(Long clienteId) {
        return repository.findAllByClienteDonoIdOrderByTagAsc(clienteId);
    }

    public ServicoTabela salvar(Long clienteId, String tag, BigDecimal valor) {

        Optional<ServicoTabela> existente = repository.findByClienteDonoIdAndTagIgnoreCase(clienteId, tag);
        ServicoTabela servico = existente.orElse(new ServicoTabela());
        servico.setTag(tag);
        servico.setValor(valor);
        if (servico.getClienteDono() == null) {
            clienteRepository.findById(clienteId).ifPresent(servico::setClienteDono);
        }
        return repository.save(servico);
    }

    public void deletar(Long clienteId, Long id) {
        repository.findByIdAndClienteDonoId(id, clienteId).ifPresent(repository::delete);
    }

    public Optional<BigDecimal> buscarValorPorTag(Long clienteId, String tag) {
        return repository.findByClienteDonoIdAndTagIgnoreCase(clienteId, tag).map(ServicoTabela::getValor);
    }
}
