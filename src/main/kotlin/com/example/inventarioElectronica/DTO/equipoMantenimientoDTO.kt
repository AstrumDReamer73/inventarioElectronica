package com.example.inventarioElectronica.DTO

import org.hibernate.validator.constraints.Length
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class equipoMantenimientoDTO(
    var IDEquipoMantenimiento: Int =0,
    var numeroSerie: String = "",
    var numeroControl: String = "",
    var estado: String = "",
    var estadoArticulo: String="",
    var descripcion: String="",

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var fechaEntrada: LocalDate = LocalDate.now(),

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var fechaSalida: LocalDate = LocalDate.now().plusDays(1),

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var fechaProximoMantenimiento: LocalDate = LocalDate.now().plusMonths(1),

    var motivo: String = "",
)