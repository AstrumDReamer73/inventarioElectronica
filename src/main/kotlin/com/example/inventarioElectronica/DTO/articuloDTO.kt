package com.example.inventarioElectronica.DTO

import java.time.LocalDate

data class articuloDTO(
    var modelo: String = "",
    var descripcion: String = "",
    var categoria: String = "",
    var marca: String = "",

    var numeroSerie: String = "",
    var estado: String = "",
    var plazoGarantia: String = "",
    var ubicacion: String = "",

    var fechaIngreso: LocalDate = LocalDate.now(),
    var fechaUltimoMantenimiento: LocalDate = LocalDate.now(),
    var fechaProximoMantenimiento: LocalDate = LocalDate.now()
)