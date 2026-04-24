package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.articuloParticular
import com.example.inventarioElectronica.Views.articulosView
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository interface articuloParticularRepository: JpaRepository<articuloParticular, String> {
    @EntityGraph(attributePaths = ["modelo"])
    fun findByNumeroSerie(id: String): articuloParticular

    @Query(value = "SP_ListarArticulos", nativeQuery = true)
    fun findAllView(): List<articulosView>

    @EntityGraph(attributePaths = ["modelo"])
    @Query("select distinct a.ubicacion from articuloParticular a")
    fun findAllUbicaciones(): List<String>

    @EntityGraph(attributePaths = ["modelo"])
    fun countByModelo_Modelo(modelo: String): Long
}