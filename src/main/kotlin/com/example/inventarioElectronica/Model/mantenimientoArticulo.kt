package com.example.inventarioElectronica.Model

import jakarta.persistence.*
import java.sql.Date
import java.time.LocalDate

@Table(name = "mantenimiento_articulo")
@Entity data class mantenimientoArticulo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var IDMantenimiento: Int=0,

    @OneToOne(fetch= FetchType.LAZY) @JoinColumn(name = "numeroSerie")
    var equipo: articuloParticular ?= null,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "numeroControl")
    var personalEncargado: usuario ?= null,

    var motivo: String="",
    var estado: String="",

    var fechaEntrada: Date = Date.valueOf(LocalDate.now()),
    var fechaSalida: Date = Date.valueOf(LocalDate.now().plusDays(1)),
    var fechaProximoMantenimiento: Date = Date.valueOf(LocalDate.now().plusMonths(1)),
)