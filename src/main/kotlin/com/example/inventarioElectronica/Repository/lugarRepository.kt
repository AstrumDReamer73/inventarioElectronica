package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.lugar
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.sql.Date
import java.sql.Time

@Repository interface lugarRepository: JpaRepository<lugar, Int> {
    @Query(value = "EXEC SP_CheckDisponibilidad ?, ?, ?, ?", nativeQuery = true)
    fun checkDisponibilidad(@Param("fecha") fecha: Date,
                            @Param("horaEntrada") horaEntrada: Time,
                            @Param("horaSalida") horaSalida: Time,
                            @Param("IDLugar") IDLugar: Int?):List<lugar>

    @Query(value = "exec SP_ListarPracticas ?", nativeQuery = true)
    fun findByMesa(@Param("IDLugar") IDLugar:Int):List<lugar>
}