package com.example.inventarioElectronica.DTO

import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class usuarioDTO(
    @field:Pattern(regexp = "^\\d+$")
    @field:Length(min=8, max=9)
    var numeroControl: String="",

    @field:Length(min=13, max=14)
    var telefono: String="",

    @field:Length(min=0, max=50)
    var nombre: String="",

    @field:Length(min=0, max=50) var correo: String="",
    var passwordHash: String="",

    var rol: String="",
)