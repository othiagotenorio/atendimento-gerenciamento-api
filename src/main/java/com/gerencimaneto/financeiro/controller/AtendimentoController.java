package com.gerencimaneto.financeiro.controller;

import org.springframework.stereotype.Controller;
import com.gerencimaneto.financeiro.model.Atendimento;
import com.gerencimaneto.financeiro.model.AtendimentoDiarioTotal;
import com.gerencimaneto.financeiro.repository.AtendimentoDiarioTotalRepository;
import com.gerencimaneto.financeiro.service.AtendimentoService;
import com.gerencimaneto.financeiro.service.ServicoTabelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/atendimentos")
public class AtendimentoController {

    @Autowired
    private AtendimentoService service;

    @Autowired
    private ServicoTabelaService servicoTabelaService;

    @Autowired
    private AtendimentoDiarioTotalRepository atendimentoDiarioTotalRepository;

    /**
     * Carrega a página filtrando por data (Histórico ou Hoje)
     * Endpoint aceita: /atendimentos?dataFiltro=2026-06-04
     */
    @GetMapping
    public String exibirAtendimentos(
            @RequestParam(value = "dataFiltro", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFiltro,
            Model model) {

        // Se a data não foi enviada pelo calendário, usamos a data de hoje por padrão
        LocalDate dataBusca = (dataFiltro != null) ? dataFiltro : LocalDate.now();

        // Busca os atendimentos do dia selecionado e da respectiva semana
        List<Atendimento> listaDia = service.listarPorData(dataBusca);
        List<Atendimento> listaSemana = service.listarPorSemana(dataBusca);

        // Filtra atendimentos pendentes (para a aba Hoje) e realizados (para a aba Histórico)
        List<Atendimento> atendimentosPendentes = listaDia.stream().filter(a -> !a.isRealizado()).toList();
        List<Atendimento> atendimentosRealizados = listaDia.stream().filter(a -> a.isRealizado()).toList();

        // Envia os dados para o HTML
        model.addAttribute("atendimentos", atendimentosPendentes);
        model.addAttribute("atendimentosRealizados", atendimentosRealizados);
        model.addAttribute("atendimentosSemana", listaSemana);

        // Devolvemos a data selecionada para manter o input do calendário preenchido
        // com o dia que foi buscado
        model.addAttribute("dataSelecionada", dataBusca);

        return "atendimentos";
    }

    @PostMapping("/salvar")
    public String salvarAtendimentoManual(@RequestParam("cliente") String cliente,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "valor", required = false) BigDecimal valor,
            @RequestParam("data") String data,
            @RequestParam("hora") String hora) {

        Atendimento atendimento = new Atendimento();
        atendimento.setCliente(cliente);
        atendimento.setDescricao(descricao);
        atendimento.setValor(valor);
        atendimento.setDataAtendimento(LocalDate.parse(data));
        atendimento.setHoraAtendimento(LocalTime.parse(hora));
        atendimento.setOrigem("MANUAL");

        service.salvarManual(atendimento);

        // Redireciona mantendo o foco na data em que o atendimento foi cadastrado
        return "redirect:/atendimentos?dataFiltro=" + data;
    }

    @PostMapping("/editar")
    public String editarAtendimento(@RequestParam("id") Long id,
            @RequestParam("cliente") String cliente,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "valor", required = false) BigDecimal valor,
            @RequestParam("data") String data,
            @RequestParam("hora") String hora) {

        service.buscarPorId(id).ifPresent(atendimento -> {
            atendimento.setCliente(cliente);
            atendimento.setDescricao(descricao);
            atendimento.setValor(valor);
            atendimento.setDataAtendimento(LocalDate.parse(data));
            atendimento.setHoraAtendimento(LocalTime.parse(hora));
            service.salvarManual(atendimento);
        });

        return "redirect:/atendimentos?dataFiltro=" + data;
    }

    @GetMapping("/deletar/{id}")
    public String deletarAtendimento(@PathVariable("id") Long id) {
        // Busca antes de deletar apenas para saber qual era a data e redirecionar para
        // o dia certo
        String dataFiltro = service.buscarPorId(id)
                .map(a -> a.getDataAtendimento().toString())
                .orElse("");

        service.excluir(id);

        return "redirect:/atendimentos" + (!dataFiltro.isEmpty() ? "?dataFiltro=" + dataFiltro : "");
    }

    @GetMapping("/realizar/{id}")
    public String realizarAtendimento(@PathVariable("id") Long id) {
        service.buscarPorId(id).ifPresent(atendimento -> {
            atendimento.setRealizado(true);
            service.salvarManual(atendimento);

            // Salva o valor, data e cliente em AtendimentoDiarioTotal
            AtendimentoDiarioTotal diarioTotal = new AtendimentoDiarioTotal(
                    atendimento.getCliente(),
                    atendimento.getDataAtendimento(),
                    atendimento.getValor() != null ? atendimento.getValor() : BigDecimal.ZERO
            );
            atendimentoDiarioTotalRepository.save(diarioTotal);
        });

        // Recupera a data para redirecionar corretamente
        String dataFiltro = service.buscarPorId(id)
                .map(a -> a.getDataAtendimento().toString())
                .orElse("");

        return "redirect:/atendimentos" + (!dataFiltro.isEmpty() ? "?dataFiltro=" + dataFiltro : "");
    }
}