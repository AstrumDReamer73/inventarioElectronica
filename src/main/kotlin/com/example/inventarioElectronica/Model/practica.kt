package com.example.inventarioElectronica.Model

import jakarta.persistence.*

@Table(name = "practicas")
@Entity data class practica(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id var IDPracticas: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "clave_materia")
    var materia: materia ?= null,

    var nombre: String = "",
    var archivo: String = "",
)