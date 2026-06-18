package com.gerencimaneto.financeiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.gerencimaneto.financeiro.model.Cliente;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    long countByStatus(String status);

    List<Cliente> findByStatusOrderByNomeEmpresaAsc(String status);

    long countByDataCadastroBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nomeEmpresa) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Cliente> buscarPorTermo(String termo);
}
