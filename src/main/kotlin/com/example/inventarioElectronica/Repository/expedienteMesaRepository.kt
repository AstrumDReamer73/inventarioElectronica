package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.expedienteMesa
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface expedienteMesaRepository: JpaRepository<expedienteMesa, Int> {
    @EntityGraph(attributePaths = ["IDLugar","numeroControl"])
    fun findByIDLugar_IDLugar(IDLugar:Int):List<expedienteMesa>
}