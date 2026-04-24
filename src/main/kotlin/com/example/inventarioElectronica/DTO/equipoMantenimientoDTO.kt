package com.example.inventarioElectronica.DTO

import jakarta.validation.constraints.Future
import org.hibernate.validator.constraints.Length
import java.sql.Date
import java.time.LocalDate

data class equipoMantenimientoDTO(
    var IDEquipoMantenimiento: Int =0,
    var numeroSerie: String = "",
    var numeroControl: String = "",
    var estado: String = "",
    var estadoArticulo: String="",
    var descripcion: String="",

    var fechaEntrada: Date = Date.valueOf(LocalDate.now()),
    var fechaSalida: Date = Date.valueOf(LocalDate.now().plusDays(1)),
    var fechaProximoMantenimiento: Date = Date.valueOf(LocalDate.now().plusMonths(1)),

    @field:Length(min=0, max=50) var motivo: String = "",
)