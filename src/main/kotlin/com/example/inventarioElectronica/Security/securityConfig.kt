package com.example.inventarioElectronica.Security

import com.example.inventarioElectronica.Model.usuario
import com.example.inventarioElectronica.Service.usuarioService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration class securityConfig (private var usuarioService: usuarioService, private val authFailureHandler: authFailureHandler) {
    @Bean fun authenticationProvider(passwordEncoder: PasswordEncoder): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider(usuarioService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

    @Bean fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http.authorizeHttpRequests {
            it.requestMatchers("/","/login","/forgotPassword","/resetPassword","/img/**").permitAll()
            it.requestMatchers("/grupos","/grupos/horario/**", "/grupos/listaGrupos/**",
                "/practicas","/usuarios","/articulos","/mantenimiento")
                .hasAnyAuthority("Docencia","Jefe","Personal","Maestro")
            it.requestMatchers("/articulos/articulo/**")
                .hasAnyAuthority("Personal","Jefe")
            it.requestMatchers("/articulos/**","/mantenimiento/**","/usuarios/usuario/**")
                .hasAnyAuthority("Personal","Jefe")
            it.requestMatchers("/usuarios/**","/admin/**")
                .hasAnyAuthority("Jefe")
            it.anyRequest().authenticated()
        }.formLogin {
            it.loginPage("/")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index", true)
                .failureHandler(authFailureHandler)
                .successHandler { _, response, authentication ->
                    val user = authentication.principal as usuario
                    usuarioService.resetAttempts(user.numeroControl)
                    response.sendRedirect("/index")
                }
                .permitAll()
        }.logout { it.permitAll() }
            .build()
}