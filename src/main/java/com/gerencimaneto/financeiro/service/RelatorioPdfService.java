package com.gerencimaneto.financeiro.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.gerencimaneto.financeiro.model.Atendimento;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RelatorioPdfService {

    public byte[] gerarRelatorioRealizados(List<Atendimento> atendimentos, String tituloPeriodo) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(252, 163, 17)); // Laranja Skink
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(33, 37, 41));
            Font fontNormalBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

            Paragraph pTitulo = new Paragraph("SKINK - SISTEMA FINANCEIRO", fontTitulo);
            pTitulo.setAlignment(Element.ALIGN_CENTER);
            pTitulo.setSpacingAfter(5);
            document.add(pTitulo);

            Paragraph pSub = new Paragraph("Relatório de Atendimentos Realizados - " + tituloPeriodo, fontSubtitulo);
            pSub.setAlignment(Element.ALIGN_CENTER);
            pSub.setSpacingAfter(20);
            document.add(pSub);

            BigDecimal totalGanho = atendimentos.stream()
                    .map(a -> a.getValor() != null ? a.getValor() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            DecimalFormat df = new DecimalFormat("R$ #,##0.00");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            PdfPTable tableResumo = new PdfPTable(2);
            tableResumo.setWidthPercentage(100);
            tableResumo.setSpacingAfter(20);
            tableResumo.setWidths(new float[] { 3f, 1f });

            PdfPCell cellTotalAtend = new PdfPCell(new Phrase("Total de Atendimentos Realizados:", fontNormalBold));
            cellTotalAtend.setBorder(Rectangle.NO_BORDER);
            tableResumo.addCell(cellTotalAtend);

            PdfPCell cellTotalAtendVal = new PdfPCell(new Phrase(String.valueOf(atendimentos.size()), fontNormal));
            cellTotalAtendVal.setBorder(Rectangle.NO_BORDER);
            cellTotalAtendVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableResumo.addCell(cellTotalAtendVal);

            PdfPCell cellFaturamento = new PdfPCell(new Phrase("Faturamento Total no Período:", fontNormalBold));
            cellFaturamento.setBorder(Rectangle.NO_BORDER);
            tableResumo.addCell(cellFaturamento);

            PdfPCell cellFaturamentoVal = new PdfPCell(new Phrase(df.format(totalGanho), fontNormalBold));
            cellFaturamentoVal.setBorder(Rectangle.NO_BORDER);
            cellFaturamentoVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableResumo.addCell(cellFaturamentoVal);

            document.add(tableResumo);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 2.0f, 2.5f, 1.8f, 1.5f, 1.2f, 1.8f }); // colunas: cliente, servicos, prof,
                                                                                 // data, hora, valor

            String[] headers = { "Cliente", "Serviço(s)", "Profissional", "Data", "Hora", "Valor" };
            Color orangeSkink = new Color(252, 163, 17);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setBackgroundColor(orangeSkink);
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            boolean alternar = false;
            Color cinzaClaro = new Color(245, 245, 245);
            for (Atendimento a : atendimentos) {
                Color bg = alternar ? cinzaClaro : Color.WHITE;
                alternar = !alternar;

                PdfPCell cCliente = new PdfPCell(new Phrase(a.getCliente(), fontNormal));
                cCliente.setBackgroundColor(bg);
                cCliente.setPadding(6);
                table.addCell(cCliente);

                PdfPCell cDesc = new PdfPCell(new Phrase(a.getDescricao(), fontNormal));
                cDesc.setBackgroundColor(bg);
                cDesc.setPadding(6);
                table.addCell(cDesc);

                String prof = a.getProfissional() != null && !a.getProfissional().isBlank() ? a.getProfissional() : "-";
                PdfPCell cProf = new PdfPCell(new Phrase(prof, fontNormal));
                cProf.setBackgroundColor(bg);
                cProf.setPadding(6);
                table.addCell(cProf);

                PdfPCell cData = new PdfPCell(new Phrase(a.getDataAtendimento().format(dateFormatter), fontNormal));
                cData.setBackgroundColor(bg);
                cData.setPadding(6);
                cData.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cData);

                PdfPCell cHora = new PdfPCell(new Phrase(a.getHoraAtendimento().format(timeFormatter), fontNormal));
                cHora.setBackgroundColor(bg);
                cHora.setPadding(6);
                cHora.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cHora);

                BigDecimal v = a.getValor() != null ? a.getValor() : BigDecimal.ZERO;
                PdfPCell cValor = new PdfPCell(new Phrase(df.format(v), fontNormalBold));
                cValor.setBackgroundColor(bg);
                cValor.setPadding(6);
                cValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cValor);
            }

            document.add(table);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
