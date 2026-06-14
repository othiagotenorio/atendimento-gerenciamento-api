package com.gerencimaneto.financeiro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servicos_tabela")
public class ServicoTabela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
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
