package com.gerencimaneto.financeiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gerencimaneto.financeiro.model.AtendimentoDiarioTotal;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AtendimentoDiarioTotalRepository extends JpaRepository<AtendimentoDiarioTotal, Long> {
    List<AtendimentoDiarioTotal> findByClienteDonoIdAndClienteAndDataAndValor(Long clienteId, String cliente, LocalDate data, BigDecimal valor);
}
