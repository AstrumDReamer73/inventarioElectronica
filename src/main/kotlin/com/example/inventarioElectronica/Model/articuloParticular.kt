package com.example.inventarioElectronica.Model

import jakarta.persistence.*
import java.time.LocalDate

@Table(name="articulos_particular")
@Entity data class articuloParticular(
    @Id var numeroSerie: String = "",

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="modelo")
    var modelo: modeloGeneral ?= null,

    var estado: String = "",
    var ubicacion: String = "",
    var plazoGarantia: String = "",
    var fechaIngreso: LocalDate = LocalDate.now(),
    var fechaUltimoMantenimiento: LocalDate = LocalDate.now(),
    var fechaProximoMantenimiento: LocalDate = LocalDate.now()
)