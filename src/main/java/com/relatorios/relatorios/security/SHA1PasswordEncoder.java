package com.relatorios.relatorios.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Encoder customizado para usar SHA1 (compatível com o banco de dados existente)
 * IMPORTANTE: SHA1 não é recomendado para novos sistemas, mas estamos usando
 * porque o banco de dados já existe com senhas em SHA1
 */
public class SHA1PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));

            // Converte bytes para hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash SHA-1", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Remove o prefixo {noop} se existir
        String cleanEncodedPassword = encodedPassword.replace("{noop}", "");
        String rawPasswordEncoded = encode(rawPassword);
        return rawPasswordEncoded.equals(cleanEncodedPassword);
    }
}