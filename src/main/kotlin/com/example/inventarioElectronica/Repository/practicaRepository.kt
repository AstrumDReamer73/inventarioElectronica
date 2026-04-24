package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.practica
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface practicaRepository: JpaRepository<practica,Int> {
    @EntityGraph(attributePaths = ["materia"])
    fun findByIDPracticas(idpractica:Int): practica

    @EntityGraph(attributePaths = ["materia"])
    fun findByMateria_ClaveMateria(claveMateria: String):List<practica>

    @EntityGraph(attributePaths = ["materia"])
    override fun findAll():List<practica>
}