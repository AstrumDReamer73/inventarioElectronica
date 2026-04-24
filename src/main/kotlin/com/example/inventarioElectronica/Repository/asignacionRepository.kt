package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.asignacionPractica
import com.example.inventarioElectronica.Views.asignacionView
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository interface asignacionRepository: JpaRepository<asignacionPractica, Int> {
    @EntityGraph(attributePaths = ["claveGrupo", "IDPractica","lugar"])
    override fun findById(id: Int): Optional<asignacionPractica>

    @EntityGraph(attributePaths = ["claveGrupo", "IDPractica", "lugar"])
    override fun findAll(): List<asignacionPractica>

    @EntityGraph(attributePaths = ["claveGrupo", "IDPractica", "lugar"])
    fun findByIDPractica_IDPracticas(IDPractica:Int): List<asignacionPractica>

    @Query("exec SP_ListarPracticas ?", nativeQuery = true)
    fun findByIDLugar_IDLugar(@Param("IDLugar") idlugar:Int):List<asignacionView>

    @Query("SELECT DISTINCT a FROM asignacionPractica a JOIN FETCH a.claveGrupo JOIN FETCH a.IDPractica WHERE a.IDPractica.IDPracticas = :IDPractica AND a.claveGrupo.claveGrupo = :claveGrupo")
    fun findByIDPractica_IDPracticasAndClaveGrupo_ClaveGrupo(@Param("IDPractica") IDPractica: Int, @Param("claveGrupo") claveGrupo: String): asignacionPractica?
}