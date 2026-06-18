package com.gerencimaneto.financeiro.service;

import com.gerencimaneto.financeiro.model.Atendimento;
import com.gerencimaneto.financeiro.model.Despesa;
import com.gerencimaneto.financeiro.repository.AtendimentoRepository;
import com.gerencimaneto.financeiro.repository.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanceiroService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    /**
     * Retorna o intervalo de datas para o período solicitado.
     */
    public LocalDate[] calcularIntervalo(String periodo, LocalDate referencia) {
        LocalDate inicio;
        LocalDate fim;
        switch (periodo.toLowerCase()) {
            case "dia":
                inicio = referencia;
                fim = referencia;
                break;
            case "semana":
                inicio = referencia.with(java.time.DayOfWeek.MONDAY);
                fim = referencia.with(java.time.DayOfWeek.SUNDAY);
                break;
            case "ano":
                inicio = referencia.withDayOfYear(1);
                fim = referencia.with(TemporalAdjusters.lastDayOfYear());
                break;
            case "mes":
            default:
                inicio = referencia.withDayOfMonth(1);
                fim = referencia.with(TemporalAdjusters.lastDayOfMonth());
                break;
        }
        return new LocalDate[]{inicio, fim};
    }

    /**
     * Busca atendimentos realizados no período.
     */
    public List<Atendimento> buscarRealizadosPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio.equals(fim)) {
            return atendimentoRepository.findByDataAtendimentoAndRealizadoTrueOrderByHoraAtendimentoAsc(inicio);
        }
        return atendimentoRepository.findByDataAtendimentoBetweenAndRealizadoTrueOrderByDataAtendimentoAscHoraAtendimentoAsc(inicio, fim);
    }

    /**
     * Calcula receita total somando valores dos atendimentos realizados.
     */
    public BigDecimal calcularReceita(List<Atendimento> atendimentos) {
        return atendimentos.stream()
                .map(a -> a.getValor() != null ? a.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula ticket médio (receita total / número de atendimentos).
     */
    public BigDecimal calcularTicketMedio(BigDecimal receita, int totalAtendimentos) {
        if (totalAtendimentos == 0) return BigDecimal.ZERO;
        return receita.divide(BigDecimal.valueOf(totalAtendimentos), 2, RoundingMode.HALF_UP);
    }

    /**
     * Ranking de clientes: agrupa por cliente e soma valores.
     * Retorna lista de [cliente, total, quantidade] ordenada por total desc.
     */
    public List<Map<String, Object>> rankingClientes(List<Atendimento> atendimentos) {
        Map<String, BigDecimal[]> mapa = new LinkedHashMap<>();

        for (Atendimento a : atendimentos) {
            String cliente = a.getCliente();
            BigDecimal val = a.getValor() != null ? a.getValor() : BigDecimal.ZERO;
            mapa.computeIfAbsent(cliente, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            mapa.get(cliente)[0] = mapa.get(cliente)[0].add(val); // total
            mapa.get(cliente)[1] = mapa.get(cliente)[1].add(BigDecimal.ONE); // qtd
        }

        return mapa.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue()[0].compareTo(e1.getValue()[0]))
                .limit(10)
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("nome", e.getKey());
                    item.put("total", e.getValue()[0]);
                    item.put("quantidade", e.getValue()[1].intValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Ranking de serviços: agrupa por descrição/serviço e soma valores.
     * Retorna lista de [servico, total, quantidade] ordenada por total desc.
     */
    public List<Map<String, Object>> rankingServicos(List<Atendimento> atendimentos) {
        Map<String, BigDecimal[]> mapa = new LinkedHashMap<>();

        for (Atendimento a : atendimentos) {
            // A descrição pode conter múltiplos serviços separados por vírgula ou + 
            String desc = a.getDescricao() != null ? a.getDescricao() : "Sem Descrição";
            BigDecimal val = a.getValor() != null ? a.getValor() : BigDecimal.ZERO;
            mapa.computeIfAbsent(desc, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            mapa.get(desc)[0] = mapa.get(desc)[0].add(val);
            mapa.get(desc)[1] = mapa.get(desc)[1].add(BigDecimal.ONE);
        }

        return mapa.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue()[0].compareTo(e1.getValue()[0]))
                .limit(10)
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("nome", e.getKey());
                    item.put("total", e.getValue()[0]);
                    item.put("quantidade", e.getValue()[1].intValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Busca despesas no período.
     */
    public List<Despesa> buscarDespesasPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio.equals(fim)) {
            return despesaRepository.findByDataOrderByDataDesc(inicio);
        }
        return despesaRepository.findByDataBetweenOrderByDataDesc(inicio, fim);
    }

    /**
     * Soma o total de despesas de uma lista.
     */
    public BigDecimal calcularTotalDespesas(List<Despesa> despesas) {
        return despesas.stream()
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula lucro líquido: receita - despesas.
     */
    public BigDecimal calcularLucroLiquido(BigDecimal receita, BigDecimal totalDespesas) {
        return receita.subtract(totalDespesas);
    }

    /**
     * Gera dados para o gráfico de ganhos por período (dia a dia dentro do intervalo).
     * Retorna Map<String(data formatada), BigDecimal(total do dia)>
     */
    public Map<String, BigDecimal> dadosGrafico(List<Atendimento> atendimentos, LocalDate inicio, LocalDate fim) {
        Map<String, BigDecimal> dados = new LinkedHashMap<>();

        // Preenche todos os dias do intervalo com zero
        LocalDate cursor = inicio;
        while (!cursor.isAfter(fim)) {
            String label = String.format("%02d/%02d", cursor.getDayOfMonth(), cursor.getMonthValue());
            dados.put(label, BigDecimal.ZERO);
            cursor = cursor.plusDays(1);
        }

        // Soma os atendimentos por dia
        for (Atendimento a : atendimentos) {
            String label = String.format("%02d/%02d",
                    a.getDataAtendimento().getDayOfMonth(),
                    a.getDataAtendimento().getMonthValue());
            BigDecimal val = a.getValor() != null ? a.getValor() : BigDecimal.ZERO;
            dados.merge(label, val, BigDecimal::add);
        }

        return dados;
    }
}
