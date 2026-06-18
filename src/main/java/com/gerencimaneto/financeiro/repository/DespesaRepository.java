package com.gerencimaneto.financeiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gerencimaneto.financeiro.model.Despesa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    Optional<Despesa> findByIdAndClienteDonoId(Long id, Long clienteId);

    List<Despesa> findByClienteDonoIdAndDataBetweenOrderByDataDesc(Long clienteId, LocalDate inicio, LocalDate fim);

    List<Despesa> findByClienteDonoIdAndDataOrderByDataDesc(Long clienteId, LocalDate data);
}
