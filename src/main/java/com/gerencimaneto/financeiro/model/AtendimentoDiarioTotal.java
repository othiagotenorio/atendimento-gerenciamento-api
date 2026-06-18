package com.gerencimaneto.financeiro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "atendimento_diario_total")
public class AtendimentoDiarioTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente clienteDono;

    @Column(nullable = false)
    private String cliente;

    @Column(nullable = false)
    private LocalDate data;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal valor;

    public AtendimentoDiarioTotal() {
    }

    public AtendimentoDiarioTotal(String cliente, LocalDate data, BigDecimal valor) {
        this.cliente = cliente;
        this.data = data;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getClienteDono() {
        return clienteDono;
    }

    public void setClienteDono(Cliente clienteDono) {
        this.clienteDono = clienteDono;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
