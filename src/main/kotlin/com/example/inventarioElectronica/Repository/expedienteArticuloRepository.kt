package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.expedienteArticulo
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface expedienteArticuloRepository: JpaRepository<expedienteArticulo, Int> {
    @EntityGraph(attributePaths = ["numeroSerie","numeroControl"])
    fun findByNumeroSerie_NumeroSerie(numeroSerie: String):List<expedienteArticulo>
}