package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.Repository.expedienteMesaRepository
import com.example.inventarioElectronica.Repository.lugarRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.sql.Date
import java.sql.Time

@Service @Transactional class lugarService(private val lugarRepository: lugarRepository, private val mesaRepository: expedienteMesaRepository) {
    fun findAll() = lugarRepository.findAll()

    fun findById(id:Int) = lugarRepository.findById(id).orElseThrow { IllegalArgumentException("La mesa no existe") }

    fun findByMesa(id:Int) = lugarRepository.findByMesa(id)

    fun checkDisponibilidad(fecha: Date,
                                  horaEntrada: Time,
                                  horaSalida: Time,
                                  IDLugar:Int?) = lugarRepository.checkDisponibilidad(fecha,horaEntrada,horaSalida, IDLugar)
}