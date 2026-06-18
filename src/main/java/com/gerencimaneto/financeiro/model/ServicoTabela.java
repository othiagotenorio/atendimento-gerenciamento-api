package com.gerencimaneto.financeiro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servicos_tabela")
public class ServicoTabela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente clienteDono;

    @Column(nullable = false, length = 100)
    private String tag;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    public ServicoTabela() {
    }

    public ServicoTabela(String tag, BigDecimal valor) {
        this.tag = tag;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag != null ? tag.toLowerCase().trim() : null;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
