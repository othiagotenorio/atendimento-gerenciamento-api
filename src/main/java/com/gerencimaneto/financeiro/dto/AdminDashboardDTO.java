package com.gerencimaneto.financeiro.dto;

public class AdminDashboardDTO {

    private long totalClientes;
    private long clientesAtivos;
    private long clientesBloqueados;
    private long novosMes;

    public AdminDashboardDTO() {
    }

    public AdminDashboardDTO(long totalClientes, long clientesAtivos, long clientesBloqueados, long novosMes) {
        this.totalClientes = totalClientes;
        this.clientesAtivos = clientesAtivos;
        this.clientesBloqueados = clientesBloqueados;
        this.novosMes = novosMes;
    }

    public long getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(long totalClientes) {
        this.totalClientes = totalClientes;
    }

    public long getClientesAtivos() {
        return clientesAtivos;
    }

    public void setClientesAtivos(long clientesAtivos) {
        this.clientesAtivos = clientesAtivos;
    }

    public long getClientesBloqueados() {
        return clientesBloqueados;
    }

    public void setClientesBloqueados(long clientesBloqueados) {
        this.clientesBloqueados = clientesBloqueados;
    }

    public long getNovosMes() {
        return novosMes;
    }

    public void setNovosMes(long novosMes) {
        this.novosMes = novosMes;
    }
}
