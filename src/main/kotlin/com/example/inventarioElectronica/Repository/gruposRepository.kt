package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.grupo
import com.example.inventarioElectronica.Views.gruposView
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.sql.Date
import java.sql.Time

@Repository interface gruposRepository : JpaRepository<grupo, String> {
    fun findByClaveMateria_ClaveMateria(claveMateria: String): List<grupo>

    @EntityGraph(attributePaths = ["numeroControl","claveMateria"])
    fun findByClaveGrupo(claveGrupo: String): grupo

    @EntityGraph(attributePaths = ["numeroControl","claveMateria"])
    fun countByClaveMateria_ClaveMateria(claveMateria: String): Int

    fun findDistinctByNumeroControl_NumeroControl(numeroControl: String): List<grupo>

    @Query(value = "EXEC SP_ListarGrupos ?", nativeQuery = true)
    fun findBySalon(salon: String): List<gruposView>

    @Query(value = "EXEC SP_InsertarGrupo ?,?,?,?", nativeQuery = true)
    fun insertGrupo(@Param("salon") salon: String,
                    @Param("horaEntrada") horaEntrada: Time,
                    @Param("horaSalida") horaSalida: Time,
                    @Param("diasLaboratorio") diasLaboratorio: String): Int

    @Query(value = "EXEC SP_UpdateGrupo ?,?,?,?,?", nativeQuery = true)
    fun updateGrupo(@Param("salon") salon: String,
                    @Param("horaEntrada") horaEntrada: Time,
                    @Param("horaSalida") horaSalida: Time,
                    @Param("diasLaboratorio") diasLaboratorio: String,
                    @Param("claveGrupo") claveGrupo: String): Int

    @Query(value = "EXEC SP_PracticasSemana ?,?,?", nativeQuery = true)
    fun findPracticasSemana(@Param("IDLugar") lugar:Int,
                            @Param("fechaInicio") fechaInicio: Date,
                            @Param("fechaTermino") fechaTermino:Date):List<gruposView>
}