package com.gerencimaneto.financeiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gerencimaneto.financeiro.model.Atendimento;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    Optional<Atendimento> findByIdAndClienteDonoId(Long id, Long clienteId);

    List<Atendimento> findByClienteDonoIdAndDataAtendimentoOrderByHoraAtendimentoAsc(Long clienteId, LocalDate data);

    List<Atendimento> findByClienteDonoIdAndDataAtendimentoBetweenOrderByDataAtendimentoAscHoraAtendimentoAsc(
            Long clienteId, LocalDate inicio, LocalDate fim);

    List<Atendimento> findByClienteDonoIdAndDataAtendimentoAndRealizadoTrueOrderByHoraAtendimentoAsc(Long clienteId,
            LocalDate data);

    List<Atendimento> findByClienteDonoIdAndDataAtendimentoBetweenAndRealizadoTrueOrderByDataAtendimentoAscHoraAtendimentoAsc(
            Long clienteId, LocalDate inicio, LocalDate fim);
}