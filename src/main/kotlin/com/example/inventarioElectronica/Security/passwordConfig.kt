package com.example.inventarioElectronica.Security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration class passwordConfig {
    @Bean fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}