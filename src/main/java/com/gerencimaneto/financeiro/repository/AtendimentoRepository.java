package com.gerencimaneto.financeiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gerencimaneto.financeiro.model.Atendimento;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    // Busca os atendimentos exatamente na data selecionada
    List<Atendimento> findByDataAtendimentoOrderByHoraAtendimentoAsc(LocalDate data);

    // Busca atendimentos em um intervalo de datas (caso use na aba semana)
    List<Atendimento> findByDataAtendimentoBetweenOrderByDataAtendimentoAscHoraAtendimentoAsc(LocalDate inicio,
            LocalDate fim);

    // Busca atendimentos realizados na data selecionada
    List<Atendimento> findByDataAtendimentoAndRealizadoTrueOrderByHoraAtendimentoAsc(LocalDate data);

    // Busca atendimentos realizados em um intervalo de datas
    List<Atendimento> findByDataAtendimentoBetweenAndRealizadoTrueOrderByDataAtendimentoAscHoraAtendimentoAsc(LocalDate inicio,
            LocalDate fim);
}