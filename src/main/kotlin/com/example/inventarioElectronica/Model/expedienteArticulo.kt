package com.example.inventarioElectronica.Model

import jakarta.persistence.*
import java.sql.Date
import java.time.LocalDate

@Table(name="expediente_articulo")
@Entity data class expedienteArticulo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var IDExpediente:Int =0,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "numero_serie")
    var numeroSerie: articuloParticular ?= null,

    @OneToOne @JoinColumn(name = "numero_control")
    var numeroControl: usuario ?= null,

    var fechaEntrada: Date = Date.valueOf(LocalDate.now()),
    var fechaSalida: Date = Date.valueOf(LocalDate.now())
)