package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.modeloGeneral
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository interface modeloGeneralRepository: JpaRepository<modeloGeneral, String> {
    @Query("select distinct m.categoria from modeloGeneral m")
    fun findAllCategorias(): List<String>
}