package com.example.inventarioElectronica.Security

import com.example.inventarioElectronica.Service.usuarioService
import io.jsonwebtoken.io.IOException
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component class authFailureHandler (private val userService: usuarioService): SimpleUrlAuthenticationFailureHandler() {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val numeroControl = request.getParameter("username") ?: ""
        val user = runCatching { userService.loadUserByUsername(numeroControl) }.getOrNull()
        val mensaje = when {
            user == null -> "Usuario no encontrado"
            exception is BadCredentialsException -> "Numero de control o contraseña incorrecta"
            else -> "Error de autenticacion"
        }
        request?.session?.setAttribute("errorMessage", mensaje)
        super.setDefaultFailureUrl("/?error")
        super.onAuthenticationFailure(request, response, exception)
    }
}