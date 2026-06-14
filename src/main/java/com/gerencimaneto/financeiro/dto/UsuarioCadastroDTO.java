package com.gerencimaneto.financeiro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioCadastroDTO {

    private String nome;
    private String email;
    private String celular;
    private String password;
}