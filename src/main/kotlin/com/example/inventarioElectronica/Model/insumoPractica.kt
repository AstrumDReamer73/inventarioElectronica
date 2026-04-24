package com.example.inventarioElectronica.Model

import jakarta.persistence.*

@Table(name="insumos_practicas")
@Entity data class insumoPractica(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var IDInsumos: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_serie", nullable = false)
    var articulo: articuloParticular ?= null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDAsignacion", nullable = false)
    var IDAsignacion: asignacionPractica ?= null,

    var estado: String = ""
)