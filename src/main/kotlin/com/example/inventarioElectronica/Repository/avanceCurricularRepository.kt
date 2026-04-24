package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.avanceCurricular
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface avanceCurricularRepository: JpaRepository<avanceCurricular, Int> {
    @EntityGraph(attributePaths = ["claveGrupo", "IDPractica"])
    fun findByclaveGrupo_claveGrupo(claveGrupo: String): List<avanceCurricular>

    @EntityGraph(attributePaths = ["claveGrupo","IDPractica"])
    fun findByIDPractica_IDPracticas(Id:Int): List<avanceCurricular>

    @EntityGraph(attributePaths = ["claveGrupo","IDPractica"])
    fun findByIDPractica_IDPracticasAndClaveGrupo_ClaveGrupo(id: Int, grupo: String): avanceCurricular?

    @EntityGraph(attributePaths = ["claveGrupo", "IDPractica"])
    override fun findAll(): List<avanceCurricular>
}