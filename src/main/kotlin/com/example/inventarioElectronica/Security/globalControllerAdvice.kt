package com.example.inventarioElectronica.Security

import com.example.inventarioElectronica.Model.usuario
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice class globalControllerAdvice {
    @ModelAttribute("usuarioActual")
    fun usuarioActual(): usuario?{
        val auth= SecurityContextHolder.getContext().authentication
        if(auth==null||auth.principal=="anonymousUser"){ return null }
        return auth.principal as usuario
    }
}