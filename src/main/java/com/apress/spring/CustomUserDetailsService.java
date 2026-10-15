package com.apress.spring;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Importante: el campo role en DB debe ser "USER" o "ADMIN" (sin el prefijo ROLE_)
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole()) // Spring le agrega automáticamente el prefijo ROLE_
                .build();
    }
}
