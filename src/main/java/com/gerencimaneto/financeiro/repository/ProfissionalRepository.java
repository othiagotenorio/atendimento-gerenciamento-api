package com.gerencimaneto.financeiro.repository;

import com.gerencimaneto.financeiro.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    
    Optional<Profissional> findByIdAndClienteDonoId(Long id, Long clienteId);

    List<Profissional> findAllByClienteDonoIdOrderByNomeAsc(Long clienteId);
}
