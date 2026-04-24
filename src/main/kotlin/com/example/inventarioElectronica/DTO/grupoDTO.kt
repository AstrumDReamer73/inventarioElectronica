package com.example.inventarioElectronica.DTO

import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length
import java.time.LocalTime

data class grupoDTO(
    @field:Length(min=4, max=4) var claveMateria: String = "",
    @field:Length(min=0, max=50) var nombreMateria: String = "",
    @field:Length(min=4, max=4) var claveGrupo: String = "",
    @field:NotEmpty var dias:List<String> = emptyList(),

    var salon: String = "",
    var numeroControl: String = "",
    var horaEntrada: LocalTime = LocalTime.now(),
    var horaSalida: LocalTime = LocalTime.now(),
)