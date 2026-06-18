package com.gerencimaneto.financeiro.controller;

import com.gerencimaneto.financeiro.model.Atendimento;
import com.gerencimaneto.financeiro.model.Despesa;
import com.gerencimaneto.financeiro.repository.DespesaRepository;
import com.gerencimaneto.financeiro.service.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;

    @Autowired
    private DespesaRepository despesaRepository;

    /**
     * Página principal do painel financeiro.
     */
    @GetMapping
    public String exibirFinanceiro(
            @RequestParam(value = "periodo", defaultValue = "mes") String periodo,
            @RequestParam(value = "data", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model) {

        LocalDate referencia = (data != null) ? data : LocalDate.now();
        LocalDate[] intervalo = financeiroService.calcularIntervalo(periodo, referencia);
        LocalDate inicio = intervalo[0];
        LocalDate fim = intervalo[1];

        // Dados financeiros
        List<Atendimento> atendimentos = financeiroService.buscarRealizadosPorPeriodo(inicio, fim);
        BigDecimal receita = financeiroService.calcularReceita(atendimentos);
        BigDecimal ticketMedio = financeiroService.calcularTicketMedio(receita, atendimentos.size());

        List<Despesa> despesas = financeiroService.buscarDespesasPorPeriodo(inicio, fim);
        BigDecimal totalDespesas = financeiroService.calcularTotalDespesas(despesas);
        BigDecimal lucroLiquido = financeiroService.calcularLucroLiquido(receita, totalDespesas);

        List<Map<String, Object>> rankingClientes = financeiroService.rankingClientes(atendimentos);
        List<Map<String, Object>> rankingServicos = financeiroService.rankingServicos(atendimentos);

        Map<String, BigDecimal> dadosGrafico = financeiroService.dadosGrafico(atendimentos, inicio, fim);

        // Serializa dados do gráfico para JSON (labels e valores separados)
        StringBuilder labelsJson = new StringBuilder("[");
        StringBuilder valoresJson = new StringBuilder("[");
        boolean primeiro = true;
        for (Map.Entry<String, BigDecimal> entry : dadosGrafico.entrySet()) {
            if (!primeiro) {
                labelsJson.append(",");
                valoresJson.append(",");
            }
            labelsJson.append("\"").append(entry.getKey()).append("\"");
            valoresJson.append(entry.getValue().toPlainString());
            primeiro = false;
        }
        labelsJson.append("]");
        valoresJson.append("]");

        // Dados para gráfico de serviços (doughnut)
        StringBuilder servicoLabels = new StringBuilder("[");
        StringBuilder servicoValores = new StringBuilder("[");
        primeiro = true;
        for (Map<String, Object> s : rankingServicos) {
            if (!primeiro) { servicoLabels.append(","); servicoValores.append(","); }
            // Limita o nome do serviço para não quebrar o gráfico
            String nome = ((String) s.get("nome")).length() > 30
                    ? ((String) s.get("nome")).substring(0, 30) + "..."
                    : (String) s.get("nome");
            servicoLabels.append("\"").append(nome.replace("\"", "'")).append("\"");
            servicoValores.append(((BigDecimal) s.get("total")).toPlainString());
            primeiro = false;
        }
        servicoLabels.append("]");
        servicoValores.append("]");

        model.addAttribute("periodo", periodo);
        model.addAttribute("referencia", referencia);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("receita", receita);
        model.addAttribute("ticketMedio", ticketMedio);
        model.addAttribute("totalDespesas", totalDespesas);
        model.addAttribute("lucroLiquido", lucroLiquido);
        model.addAttribute("totalAtendimentos", atendimentos.size());
        model.addAttribute("rankingClientes", rankingClientes);
        model.addAttribute("rankingServicos", rankingServicos);
        model.addAttribute("despesas", despesas);
        model.addAttribute("graficoLabels", labelsJson.toString());
        model.addAttribute("graficoValores", valoresJson.toString());
        model.addAttribute("servicoLabels", servicoLabels.toString());
        model.addAttribute("servicoValores", servicoValores.toString());

        return "financeiro";
    }

    /**
     * Salva uma nova despesa.
     */
    @PostMapping("/despesas/salvar")
    public String salvarDespesa(
            @RequestParam("descricao") String descricao,
            @RequestParam("valor") BigDecimal valor,
            @RequestParam("data") String data,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "periodo", defaultValue = "mes") String periodo,
            @RequestParam(value = "referencia", required = false) String referencia) {

        Despesa despesa = new Despesa(descricao, valor, LocalDate.parse(data), categoria);
        despesaRepository.save(despesa);

        String redirect = "/financeiro?periodo=" + periodo;
        if (referencia != null && !referencia.isBlank()) {
            redirect += "&data=" + referencia;
        }
        return "redirect:" + redirect;
    }

    /**
     * Exclui uma despesa.
     */
    @GetMapping("/despesas/deletar/{id}")
    public String deletarDespesa(
            @PathVariable("id") Long id,
            @RequestParam(value = "periodo", defaultValue = "mes") String periodo,
            @RequestParam(value = "referencia", required = false) String referencia) {

        despesaRepository.deleteById(id);

        String redirect = "/financeiro?periodo=" + periodo;
        if (referencia != null && !referencia.isBlank()) {
            redirect += "&data=" + referencia;
        }
        return "redirect:" + redirect;
    }
}
