package com.gerencimaneto.financeiro.controller;

import org.springframework.stereotype.Controller;
import com.gerencimaneto.financeiro.model.Atendimento;
import com.gerencimaneto.financeiro.model.AtendimentoDiarioTotal;
import com.gerencimaneto.financeiro.repository.AtendimentoDiarioTotalRepository;
import com.gerencimaneto.financeiro.repository.AtendimentoRepository;
import com.gerencimaneto.financeiro.service.AtendimentoService;
import com.gerencimaneto.financeiro.service.ServicoTabelaService;
import com.gerencimaneto.financeiro.service.RelatorioPdfService;
import com.gerencimaneto.financeiro.repository.ProfissionalRepository;
import com.gerencimaneto.financeiro.model.Profissional;
import com.gerencimaneto.financeiro.repository.ClienteRepository;
import com.gerencimaneto.financeiro.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

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
    private AtendimentoRepository repository;

    @Autowired
    private ServicoTabelaService servicoTabelaService;

    @Autowired
    private AtendimentoDiarioTotalRepository atendimentoDiarioTotalRepository;

    @Autowired
    private RelatorioPdfService relatorioPdfService;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Carrega a página filtrando por data (Histórico ou Hoje)
     * Endpoint aceita: /atendimentos?dataFiltro=2026-06-04
     */
    @GetMapping
    public String exibirAtendimentos(
            HttpSession session,
            @RequestParam(value = "dataFiltro", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFiltro,
            @RequestParam(value = "profissionalFiltro", required = false) String profissionalFiltro,
            Model model) {

        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) return "redirect:/login";

        // Se a data não foi enviada pelo calendário, usamos a data de hoje por padrão
        LocalDate dataBusca = (dataFiltro != null) ? dataFiltro : LocalDate.now();

        // Busca os atendimentos do dia selecionado e da respectiva semana
        List<Atendimento> listaDia = service.listarPorData(clienteId, dataBusca);
        List<Atendimento> listaSemana = service.listarPorSemana(clienteId, dataBusca);

        // Filtra atendimentos pendentes (para a aba Hoje) e realizados (para a aba Histórico)
        List<Atendimento> atendimentosPendentes = listaDia.stream().filter(a -> !a.isRealizado()).toList();
        
        List<Atendimento> atendimentosRealizados = listaDia.stream()
            .filter(Atendimento::isRealizado)
            .filter(a -> profissionalFiltro == null || profissionalFiltro.isBlank() || 
                        (a.getProfissional() != null && a.getProfissional().equalsIgnoreCase(profissionalFiltro)))
            .toList();

        // Envia os dados para o HTML
        model.addAttribute("atendimentos", atendimentosPendentes);
        model.addAttribute("atendimentosRealizados", atendimentosRealizados);
        model.addAttribute("atendimentosSemana", listaSemana);
        model.addAttribute("profissionais", profissionalRepository.findAllByClienteDonoIdOrderByNomeAsc(clienteId));
        model.addAttribute("profissionalFiltro", profissionalFiltro);

        // Devolvemos a data selecionada para manter o input do calendário preenchido
        // com o dia que foi buscado
        model.addAttribute("dataSelecionada", dataBusca);

        return "atendimentos";
    }

    @PostMapping("/salvar")
    public String salvarAtendimentoManual(
            HttpSession session,
            @RequestParam("cliente") String cliente,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "valor", required = false) BigDecimal valor,
            @RequestParam("data") String data,
            @RequestParam("hora") String hora,
            @RequestParam(value = "profissional", required = true) String profissional) {

        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) return "redirect:/login";

        Atendimento atendimento = new Atendimento();
        atendimento.setCliente(cliente);
        atendimento.setDescricao(descricao);
        atendimento.setValor(valor);
        atendimento.setDataAtendimento(LocalDate.parse(data));
        atendimento.setHoraAtendimento(LocalTime.parse(hora));
        atendimento.setOrigem("MANUAL");
        atendimento.setProfissional(profissional != null && !profissional.isBlank() ? profissional.trim() : null);
        
        clienteRepository.findById(clienteId).ifPresent(atendimento::setClienteDono);

        service.salvarManual(atendimento);

        // Redireciona mantendo o foco na data em que o atendimento foi cadastrado
        return "redirect:/atendimentos?dataFiltro=" + data;
    }

    @PostMapping("/editar")
    public String editarAtendimento(
            HttpSession session,
            @RequestParam("id") Long id,
            @RequestParam("cliente") String cliente,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "valor", required = false) BigDecimal valor,
            @RequestParam("data") String data,
            @RequestParam("hora") String hora,
            @RequestParam(value = "profissional", required = true) String profissional) {

        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) return "redirect:/login";

        final String prof = (profissional != null && !profissional.isBlank()) ? profissional.trim() : null;
        service.buscarPorIdECliente(id, clienteId).ifPresent(atendimento -> {
            atendimento.setCliente(cliente);
            atendimento.setDescricao(descricao);
            atendimento.setValor(valor);
            atendimento.setDataAtendimento(LocalDate.parse(data));
            atendimento.setHoraAtendimento(LocalTime.parse(hora));
            atendimento.setProfissional(prof);
            service.salvarManual(atendimento);
        });

        return "redirect:/atendimentos?dataFiltro=" + data;
    }

    @GetMapping("/deletar/{id}")
    public String deletarAtendimento(HttpSession session, @PathVariable("id") Long id) {
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) return "redirect:/login";

        java.util.Optional<Atendimento> opt = service.buscarPorIdECliente(id, clienteId);
        String dataFiltro = "";

        if (opt.isPresent()) {
            Atendimento atend = opt.get();
            dataFiltro = atend.getDataAtendimento().toString();

            // Se o atendimento já estava realizado, limpa o total correspondente
            if (atend.isRealizado()) {
                BigDecimal val = atend.getValor() != null ? atend.getValor() : BigDecimal.ZERO;
                List<AtendimentoDiarioTotal> matching = atendimentoDiarioTotalRepository
                        .findByClienteDonoIdAndClienteAndDataAndValor(clienteId, atend.getCliente(), atend.getDataAtendimento(), val);
                if (!matching.isEmpty()) {
                    atendimentoDiarioTotalRepository.delete(matching.get(0));
                }
            }
            service.excluirSePertenceAoCliente(id, clienteId);
        }

        return "redirect:/atendimentos" + (!dataFiltro.isEmpty() ? "?dataFiltro=" + dataFiltro : "");
    }

    @GetMapping("/reverter/{id}")
    public String reverterAtendimento(HttpSession session, @PathVariable("id") Long id) {
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) return "redirect:/login";

        service.buscarPorIdECliente(id, clienteId).ifPresent(atendimento -> {
            atendimento.setRealizado(false);
            service.salvarManual(atendimento);

            // Remove o registro correspondente em AtendimentoDiarioTotal
            BigDecimal val = atendimento.getValor() != null ? atendimento.getValor() : BigDecimal.ZERO;
            List<AtendimentoDiarioTotal> matching = atendimentoDiarioTotalRepository
                    .findByClienteDonoIdAndClienteAndDataAndValor(clienteId, atendimento.getCliente(), atendimento.getDataAtendimento(), val);
            if (!matching.isEmpty()) {
                atendimentoDiarioTotalRepository.delete(matching.get(0));
            }
        });

        String dataFiltro = service.buscarPorId(id)
                .map(a -> a.getDataAtendimento().toString())
                .orElse("");

        return "redirect:/atendimentos" + (!dataFiltro.isEmpty() ? "?dataFiltro=" + dataFiltro : "");
    }

    @GetMapping("/realizar/{id}")
    public String realizarAtendimento(HttpSession session, @PathVariable("id") Long id) {
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) return "redirect:/login";

        service.buscarPorIdECliente(id, clienteId).ifPresent(atendimento -> {
            atendimento.setRealizado(true);
            service.salvarManual(atendimento);

            // Salva o valor, data e cliente em AtendimentoDiarioTotal
            AtendimentoDiarioTotal diarioTotal = new AtendimentoDiarioTotal(
                    atendimento.getCliente(),
                    atendimento.getDataAtendimento(),
                    atendimento.getValor() != null ? atendimento.getValor() : BigDecimal.ZERO
            );
            clienteRepository.findById(clienteId).ifPresent(diarioTotal::setClienteDono);
            atendimentoDiarioTotalRepository.save(diarioTotal);
        });

        // Recupera a data para redirecionar corretamente
        String dataFiltro = service.buscarPorIdECliente(id, clienteId)
                .map(a -> a.getDataAtendimento().toString())
                .orElse("");

        return "redirect:/atendimentos" + (!dataFiltro.isEmpty() ? "?dataFiltro=" + dataFiltro : "");
    }

    @GetMapping("/relatorio")
    public void baixarRelatorio(
            HttpSession session,
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "data", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(value = "profissional", required = false) String profissional,
            jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {

        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) return;

        if (data == null) {
            data = LocalDate.now();
        }

        List<Atendimento> atendimentos;
        String tituloPeriodo;

        switch (tipo.toLowerCase()) {
            case "semanal":
                LocalDate fimSemana = data.plusDays(6);
                atendimentos = repository.findByClienteDonoIdAndDataAtendimentoBetweenAndRealizadoTrueOrderByDataAtendimentoAscHoraAtendimentoAsc(clienteId, data, fimSemana);
                tituloPeriodo = "Semanal (" + data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " + fimSemana.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")";
                break;
            case "mensal":
                LocalDate inicioMes = data.withDayOfMonth(1);
                LocalDate fimMes = data.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
                atendimentos = repository.findByClienteDonoIdAndDataAtendimentoBetweenAndRealizadoTrueOrderByDataAtendimentoAscHoraAtendimentoAsc(clienteId, inicioMes, fimMes);
                tituloPeriodo = "Mensal (" + data.format(java.time.format.DateTimeFormatter.ofPattern("MMMM/yyyy", new java.util.Locale("pt", "BR"))) + ")";
                break;
            case "anual":
                LocalDate inicioAno = data.withDayOfYear(1);
                LocalDate fimAno = data.with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
                atendimentos = repository.findByClienteDonoIdAndDataAtendimentoBetweenAndRealizadoTrueOrderByDataAtendimentoAscHoraAtendimentoAsc(clienteId, inicioAno, fimAno);
                tituloPeriodo = "Anual (" + data.getYear() + ")";
                break;
            case "diario":
            default:
                atendimentos = repository.findByClienteDonoIdAndDataAtendimentoAndRealizadoTrueOrderByHoraAtendimentoAsc(clienteId, data);
                tituloPeriodo = "Diário (" + data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")";
                break;
        }

        if (profissional != null && !profissional.isBlank()) {
            atendimentos = atendimentos.stream()
                    .filter(a -> a.getProfissional() != null && a.getProfissional().equalsIgnoreCase(profissional))
                    .toList();
            tituloPeriodo += " (Profissional: " + profissional + ")";
        }

        byte[] pdfBytes = relatorioPdfService.gerarRelatorioRealizados(atendimentos, tituloPeriodo);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"relatorio_atendimentos_" + tipo + ".pdf\"");
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}