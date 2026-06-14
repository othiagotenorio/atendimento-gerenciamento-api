package com.gerencimaneto.financeiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gerencimaneto.financeiro.model.AtendimentoDiarioTotal;

@Repository
public interface AtendimentoDiarioTotalRepository extends JpaRepository<AtendimentoDiarioTotal, Long> {
}
