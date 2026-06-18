package com.gerencimaneto.financeiro.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOME_EMPRESA", nullable = false)
    private String nomeEmpresa;

    @Column(name = "NOME_RESPONSAVEL", nullable = false)
    private String nomeResponsavel;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "TELEFONE")
    private String telefone;

    @Column(name = "PLANO")
    private String plano; // Ex: "BÁSICO", "PROFISSIONAL", "ENTERPRISE"

    @Column(name = "STATUS", nullable = false)
    private String status; // "ATIVO" ou "BLOQUEADO"

    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDate dataCadastro;

    // ── Campos de autenticação ──────────────────────────────
    @Column(name = "SENHA")
    private String senha;

    @Column(name = "CPF", length = 20)
    private String cpf;

    /**
     * Indica se o cliente ainda não trocou a senha inicial definida pelo admin.
     * true  → ao logar, será redirecionado para /trocar-senha (obrigatório)
     * false → acesso normal ao sistema
     */
    @Column(name = "PRIMEIRO_ACESSO", nullable = false, columnDefinition = "boolean default true")
    private boolean primeiroAcesso = true;

    public Cliente() {
        this.status = "ATIVO";
        this.dataCadastro = LocalDate.now();
        this.primeiroAcesso = true;
    }

    // ── Getters e Setters ──────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }

    public String getNomeResponsavel() { return nomeResponsavel; }
    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getPlano() { return plano; }
    public void setPlano(String plano) { this.plano = plano; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public boolean isPrimeiroAcesso() { return primeiroAcesso; }
    public void setPrimeiroAcesso(boolean primeiroAcesso) { this.primeiroAcesso = primeiroAcesso; }
}
