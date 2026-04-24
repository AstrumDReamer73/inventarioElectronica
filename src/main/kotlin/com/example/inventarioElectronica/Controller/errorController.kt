package com.example.inventarioElectronica.Controller

import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice class errorController {
    @ExceptionHandler(IllegalArgumentException::class)
    fun manejarExcepcion(e: Exception, model: Model): String {
        model.addAttribute("error", e.message)
        return "error"
    }
}