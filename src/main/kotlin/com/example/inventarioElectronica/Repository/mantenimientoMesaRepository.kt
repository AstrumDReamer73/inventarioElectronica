package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.mantenimientoMesa
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface mantenimientoMesaRepository: JpaRepository<mantenimientoMesa,Int> {
    @EntityGraph(attributePaths = ["IDLugar"])
    fun findByIDLugar_IDLugar(IDLugar:Int):mantenimientoMesa
}