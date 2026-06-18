package com.gerencimaneto.financeiro.repository;

import com.gerencimaneto.financeiro.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    List<Profissional> findAllByOrderByNomeAsc();
}
