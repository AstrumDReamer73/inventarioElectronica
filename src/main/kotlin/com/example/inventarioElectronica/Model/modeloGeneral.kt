package com.example.inventarioElectronica.Model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name="modelos_general")
@Entity class modeloGeneral (
    @Id var modelo: String = "",
    var descripcion: String = "",
    var marca: String = "",
    var categoria: String = "",
)