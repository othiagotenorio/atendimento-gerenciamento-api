package com.gerencimaneto.financeiro.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "CELULAR")
    private String celular;

    @Column(name = "SENHA")
    private String senha;

    @Column(name = "PERFIL")
    private String perfil; // "ADMIN" ou "USER"
}
