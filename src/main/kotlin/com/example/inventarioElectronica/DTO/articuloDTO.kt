package com.example.inventarioElectronica.DTO

import org.hibernate.validator.constraints.Length
import java.sql.Date
import java.time.LocalDate

data class articuloDTO(
    @field:Length(min=0, max=50) var numeroSerie: String ="",
    @field:Length(min=0, max=50) var plazoGarantia: String ="",
    @field:Length(min=0, max=50) var ubicacion: String ="",

    @field:Length(min=0, max=50) var modelo: String ="",
    @field:Length(min=0, max=50) var marca: String ="",
    @field:Length(min=0, max=50) var categoria: String ="",
    var descripcion: String ="",

    var estado: String ="",
    var fechaIngreso: Date? = Date.valueOf(LocalDate.now()),
    var fechaUltimoMantenimiento: Date? = Date.valueOf(LocalDate.now()),
    var fechaProximoMantenimiento: Date? = Date.valueOf(LocalDate.now().plusMonths(1)),
)