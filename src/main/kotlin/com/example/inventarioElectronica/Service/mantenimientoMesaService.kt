package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.DTO.lugarDTO
import com.example.inventarioElectronica.Model.expedienteMesa
import com.example.inventarioElectronica.Model.mantenimientoMesa
import com.example.inventarioElectronica.Repository.expedienteMesaRepository
import com.example.inventarioElectronica.Repository.lugarRepository
import com.example.inventarioElectronica.Repository.mantenimientoMesaRepository
import com.example.inventarioElectronica.Repository.usuarioRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service @Transactional class mantenimientoMesaService(
    private val expedienteMesaRepository: expedienteMesaRepository,
    private val mantenimientoMesaRepository: mantenimientoMesaRepository,
    private val lugarRepository: lugarRepository,
    private val usuariosRepository: usuarioRepository
) {
    fun findByID(id:Int) = mantenimientoMesaRepository.findByIDLugar_IDLugar(id)

    fun mantenimiento(lugarDTO: lugarDTO){
        val lugar = lugarRepository.findById(lugarDTO.idlugar)
            .orElseThrow { IllegalArgumentException("La mesa de trabajo no esta disponible") }
        val usuario = usuariosRepository.findById(lugarDTO.numeroControl)
            .orElseThrow { IllegalArgumentException("El personal encargado no existe") }

        require(lugarDTO.fechaSalida >= lugarDTO.fechaEntrada)
        { "La fecha de salida debe ser igual o posterior a la fecha de entrada" }
        require(lugarDTO.fechaSalida <= lugarDTO.fechaProximoMantenimiento)
        { "La fecha de salida no puede ser después de la fecha del próximo mantenimiento" }
        require(lugarDTO.fechaProximoMantenimiento >= lugarDTO.fechaUltimoMantenimiento)
        { "La fecha del próximo mantenimiento debe ser posterior al último mantenimiento" }

        lugar.apply {
            estado ="En mantenimiento"
            fechaProximoMantenimiento = lugarDTO.fechaProximoMantenimiento
            nombrePersonal = usuario.nombre
            fechaSalidaMantenimiento = lugarDTO.fechaSalida
        }
        mantenimientoMesaRepository.save(
            mantenimientoMesa(IDLugar = lugar,
                                      usuario = usuario,
                                      fechaEntrada = lugarDTO.fechaEntrada,
                                      fechaSalida = lugarDTO.fechaSalida,
                                      fechaProximoMantenimiento = lugarDTO.fechaProximoMantenimiento)
        )
        lugarRepository.save(lugar)
    }

    fun habilitar(dto: lugarDTO){
        val lugar = lugarRepository.findById(dto.idlugar)
            .orElseThrow { IllegalArgumentException("La mesa de trabajo no esta disponible") }
        val mantenimientoMesa = mantenimientoMesaRepository.findByIDLugar_IDLugar(dto.idlugar)
        val usuario = usuariosRepository.findById(dto.numeroControl)
            .orElseThrow { IllegalArgumentException("El usuario no fue encontrado") }

        require(LocalDate.now() >= lugar.fechaSalidaMantenimiento!!.toLocalDate())
        { "Para poder marcar un articulo como finalizado, la fecha de hoy debe ser despues de la fecha de salida" }

        lugar.apply {
            estado ="Disponible"
            fechaUltimoMantenimiento = dto.fechaUltimoMantenimiento
            fechaProximoMantenimiento = fechaProximoMantenimiento
        }
        mantenimientoMesaRepository.delete(mantenimientoMesa)
        lugarRepository.save(lugar)
        expedienteMesaRepository.save(
            expedienteMesa(IDLugar = lugar,
                                   numeroControl = usuario,
                                   fechaEntrada = dto.fechaEntrada,
                                   fechaSalida = dto.fechaSalida,
                                   fechaProximoMantenimiento = dto.fechaProximoMantenimiento)
        )
    }

    fun expediente(idLugar:Int) = expedienteMesaRepository.findByIDLugar_IDLugar(idLugar)
}