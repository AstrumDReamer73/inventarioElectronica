package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.Model.materia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository interface materiasRepository: JpaRepository<materia, String> {
    override fun findAll(): List<materia>

    override fun findById(id: String): Optional<materia>

    override fun existsById(id: String): Boolean
}