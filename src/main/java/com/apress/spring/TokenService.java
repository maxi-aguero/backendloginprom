package com.apress.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class TokenService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    /**
     * Genera y guarda un nuevo token de recuperación para un usuario.
     * Este método necesita transacción porque hace un INSERT.
     */
    @Transactional
    public String createToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // expira en 30 min
        tokenRepository.save(resetToken);
        return token;
    }

    /**
     * Valida un token de recuperación.
     * Solo lectura, por eso se marca como readOnly.
     */
    @Transactional(readOnly = true)
    public UserEntity validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElse(null);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return null; // inválido o expirado
        }
        return resetToken.getUser();
    }

    /**
     * Invalida (elimina) un token de recuperación.
     * Necesita transacción porque hace un DELETE.
     */
    @Transactional
    public void invalidateToken(String token) {
        tokenRepository.deleteByToken(token);
    }
}


