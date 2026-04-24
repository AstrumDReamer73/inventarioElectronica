package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.mantenimientoArticulo
import com.example.inventarioElectronica.Views.equiposMantenimientoView
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository interface mantenimientoArticuloRepository: JpaRepository<mantenimientoArticulo, Int> {
    @EntityGraph(attributePaths = ["equipo", "personalEncargado", "equipo.modelo"])
    override fun findById(id: Int): Optional<mantenimientoArticulo>

    @Query(value = "EXEC SP_ListarEquiposMantenimiento", nativeQuery = true)
    fun findAllView(): List<equiposMantenimientoView>
}