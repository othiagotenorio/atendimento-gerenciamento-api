package com.gerencimaneto.financeiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gerencimaneto.financeiro.model.ServicoTabela;

import java.util.Optional;
import java.util.List;

@Repository
public interface ServicoTabelaRepository extends JpaRepository<ServicoTabela, Long> {

    Optional<ServicoTabela> findByIdAndClienteDonoId(Long id, Long clienteId);

    Optional<ServicoTabela> findByClienteDonoIdAndTagIgnoreCase(Long clienteId, String tag);

    List<ServicoTabela> findAllByClienteDonoIdOrderByTagAsc(Long clienteId);
}
