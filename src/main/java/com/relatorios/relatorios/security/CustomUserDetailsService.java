package com.relatorios.relatorios.security;

import com.relatorios.relatorios.model.Usuario;
import com.relatorios.relatorios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Serviço customizado para carregar usuários do banco de dados
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Retorna o UserDetails do Spring Security
        // A senha já está em SHA1 no banco, então usamos {noop} para indicar que não precisa criptografar novamente
        return User.builder()
                .username(usuario.getEmail())
                .password("{noop}" + usuario.getSenha()) // {noop} = no operation (sem encoding adicional)
                .authorities(new ArrayList<>()) // Sem roles específicas para este projeto
                .build();
    }

    /**
     * Busca o usuário completo pelo email
     */
    public Usuario findUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
}