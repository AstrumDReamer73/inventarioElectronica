package com.example.inventarioElectronica.Model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "materias")
@Entity data class materia(
    @Id var claveMateria: String = "",
    var nombreMateria: String = "",
)