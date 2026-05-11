package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.articuloParticular
import com.example.inventarioElectronica.Model.insumoPractica
import com.example.inventarioElectronica.Views.insumosView
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.sql.Date
import java.util.Optional

@Repository interface insumosRepository: JpaRepository<insumoPractica, Int> {
    @EntityGraph(attributePaths = ["articulo", "articulo.modelo", "IDAsignacion"])
    fun findByIDAsignacion_IDAsignacion(id: Int): List<insumoPractica>

    @EntityGraph(attributePaths = ["articulo", "articulo.modelo", "IDAsignacion"])
    override fun findById(id: Int): Optional<insumoPractica>

    @Query(value ="exec SP_ListarInsumos ?,?", nativeQuery = true)
    fun findArticulosElegibles(@Param("fechaInicio") fechaInicio: Date,
                               @Param("fechaTermino") fechaTermino: Date): List<insumosView>
}