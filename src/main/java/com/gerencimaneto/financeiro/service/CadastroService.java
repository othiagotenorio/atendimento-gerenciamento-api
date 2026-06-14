package com.gerencimaneto.financeiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gerencimaneto.financeiro.dto.UsuarioCadastroDTO;
import com.gerencimaneto.financeiro.model.Usuario;
import com.gerencimaneto.financeiro.repository.UsuarioRepository;

import java.util.Optional;

@Service
public class CadastroService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void cadastrarNovoUsuario(UsuarioCadastroDTO dto) {

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(dto.getEmail());
        if (usuarioExistente.isPresent()) {
            // Lançamos uma exceção para o Controller capturar e exibir na tela
            throw new IllegalArgumentException("Este e-mail já está cadastrado no Calango!");
        }

        Usuario novoUsuario = converterDToParaEntidade(dto);

        // Persistência
        usuarioRepository.save(novoUsuario);
    }

    private Usuario converterDToParaEntidade(UsuarioCadastroDTO dto) {

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCelular(dto.getCelular());
        usuario.setSenha(dto.getPassword());
        return usuario;
    }

}