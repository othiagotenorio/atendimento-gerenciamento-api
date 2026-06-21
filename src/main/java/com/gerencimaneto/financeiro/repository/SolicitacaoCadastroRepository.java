package com.gerencimaneto.financeiro.repository;

import com.gerencimaneto.financeiro.model.SolicitacaoCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoCadastroRepository extends JpaRepository<SolicitacaoCadastro, Long> {

    List<SolicitacaoCadastro> findAllByOrderByDataSolicitacaoDesc();

    List<SolicitacaoCadastro> findByStatusOrderByDataSolicitacaoDesc(SolicitacaoCadastro.Status status);

    long countByStatus(SolicitacaoCadastro.Status status);
}
