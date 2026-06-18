package com.gerencimaneto.financeiro.dto;

import com.gerencimaneto.financeiro.model.Cliente;

public class ClienteDetalheDTO {

    private Cliente cliente;
    private long qtdUsuarios;
    private long qtdAtendimentos;
    private long qtdServicos;

    public ClienteDetalheDTO() {
    }

    public ClienteDetalheDTO(Cliente cliente, long qtdUsuarios, long qtdAtendimentos, long qtdServicos) {
        this.cliente = cliente;
        this.qtdUsuarios = qtdUsuarios;
        this.qtdAtendimentos = qtdAtendimentos;
        this.qtdServicos = qtdServicos;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public long getQtdUsuarios() {
        return qtdUsuarios;
    }

    public void setQtdUsuarios(long qtdUsuarios) {
        this.qtdUsuarios = qtdUsuarios;
    }

    public long getQtdAtendimentos() {
        return qtdAtendimentos;
    }

    public void setQtdAtendimentos(long qtdAtendimentos) {
        this.qtdAtendimentos = qtdAtendimentos;
    }

    public long getQtdServicos() {
        return qtdServicos;
    }

    public void setQtdServicos(long qtdServicos) {
        this.qtdServicos = qtdServicos;
    }
}
