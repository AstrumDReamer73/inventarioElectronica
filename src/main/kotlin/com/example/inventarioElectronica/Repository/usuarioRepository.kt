package com.example.inventarioElectronica.Repository

import com.example.inventarioElectronica.DTO.usuarioDTO
import com.example.inventarioElectronica.Model.usuario
import com.example.inventarioElectronica.Views.usuarioView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository interface usuarioRepository: JpaRepository<usuario, String> {
    fun findByRolIn(rol:List<String>): List<usuario>

    @Query(value = "exec SP_ListarUsuarios", nativeQuery = true)
    fun findAllUsuarios(): List<usuarioView>

    fun findByNumeroControl(numeroControl: String): usuarioDTO

    fun findByCorreo(correo: String): usuario?

    fun findByResetToken(token: String): usuario?
}