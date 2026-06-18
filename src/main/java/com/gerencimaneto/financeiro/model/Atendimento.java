package com.gerencimaneto.financeiro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "atendimentos")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cliente;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String origem; // "MANUAL" ou "WHATSAPP"

    @Column(nullable = false)
    private LocalDate dataAtendimento;

    @Column(nullable = false)
    private LocalTime horaAtendimento;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean realizado = false;

    @Column
    private String profissional; // Nome do profissional que realizou o atendimento

    public Atendimento() {
    }

    public Atendimento(String cliente, String descricao, String origem, LocalDate dataAtendimento,
            LocalTime horaAtendimento, BigDecimal valor) {
        this.cliente = cliente;
        this.descricao = descricao;
        this.origem = origem;
        this.dataAtendimento = dataAtendimento;
        this.horaAtendimento = horaAtendimento;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public LocalDate getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(LocalDate dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public String getFormatData() {
        return dataAtendimento.toString();
    }

    public LocalTime getHoraAtendimento() {
        return horaAtendimento;
    }

    public void setHoraAtendimento(LocalTime horaAtendimento) {
        this.horaAtendimento = horaAtendimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public boolean isRealizado() {
        return realizado;
    }

    public void setRealizado(boolean realizado) {
        this.realizado = realizado;
    }

    public String getProfissional() {
        return profissional;
    }

    public void setProfissional(String profissional) {
        this.profissional = profissional;
    }
}