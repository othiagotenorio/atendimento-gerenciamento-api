package com.gerencimaneto.financeiro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_solicitacao_cadastro")
public class SolicitacaoCadastro {

    public enum Status {
        PENDENTE, EM_ANALISE, APROVADO, REJEITADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_responsavel", nullable = false)
    private String nomeResponsavel;

    @Column(name = "nome_estabelecimento", nullable = false)
    private String nomeEstabelecimento;

    @Column(name = "cpf_cnpj", nullable = false, length = 30)
    private String cpfCnpj;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public SolicitacaoCadastro() {
        this.dataSolicitacao = LocalDateTime.now();
        this.status = Status.PENDENTE;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeResponsavel() { return nomeResponsavel; }
    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }

    public String getNomeEstabelecimento() { return nomeEstabelecimento; }
    public void setNomeEstabelecimento(String nomeEstabelecimento) { this.nomeEstabelecimento = nomeEstabelecimento; }

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
