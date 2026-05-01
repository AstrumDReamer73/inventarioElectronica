package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.DTO.equipoMantenimientoDTO
import com.example.inventarioElectronica.Model.expedienteArticulo
import com.example.inventarioElectronica.Model.mantenimientoArticulo
import com.example.inventarioElectronica.Repository.articuloParticularRepository
import com.example.inventarioElectronica.Repository.mantenimientoArticuloRepository
import com.example.inventarioElectronica.Repository.expedienteArticuloRepository
import com.example.inventarioElectronica.Repository.modeloGeneralRepository
import com.example.inventarioElectronica.Repository.usuarioRepository
import com.example.inventarioElectronica.Views.equiposMantenimientoView
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.sql.Date
import java.time.LocalDate

@Service @Transactional class mantenimientoArticuloService(
    private val equipoMantenimientoRepository: mantenimientoArticuloRepository,
    private val articuloParticularRepository: articuloParticularRepository,
    private val expedienteMantenimientoRepository: expedienteArticuloRepository,
    private val usuariosRepository: usuarioRepository,
    private val modeloGeneralRepository: modeloGeneralRepository
) {
    fun saveEquipoMantenimiento(equipoMantenimiento: equipoMantenimientoDTO) {
        val articulo = articuloParticularRepository.findById(equipoMantenimiento.numeroSerie)
            .orElseThrow { IllegalArgumentException("El articulo no existe") }
        val usuario = usuariosRepository.findById(equipoMantenimiento.numeroControl)
            .orElseThrow { IllegalArgumentException("El usuario no existe") }
        if(articulo.estado.equals("En mantenimiento")){ throw IllegalArgumentException("El articulo ya esta en mantenimiento") }

        require(equipoMantenimiento.fechaEntrada >= Date.valueOf(LocalDate.now()))
        { "La fecha de salida no puede ser anterior a hoy" }
        require(equipoMantenimiento.fechaEntrada <= equipoMantenimiento.fechaSalida)
        { "La fecha de entrada no puede ser después de la fecha salida" }
        require(equipoMantenimiento.fechaProximoMantenimiento >= equipoMantenimiento.fechaEntrada)
        { "La fecha del próximo mantenimiento no puede ser antes de la fecha de entrada" }
        require(equipoMantenimiento.fechaProximoMantenimiento >= equipoMantenimiento.fechaSalida)
        { "La fecha del próximo mantenimiento no puede ser antes de la salida" }
        require(equipoMantenimiento.fechaProximoMantenimiento >= articulo.fechaUltimoMantenimiento)
        { "La fecha del próximo mantenimiento no puede ser antes del último mantenimiento" }

        articulo.apply { estado = "En mantenimiento" }
        articuloParticularRepository.save(articulo)
        equipoMantenimientoRepository.save(
            mantenimientoArticulo(equipo = articulo,
                                          personalEncargado = usuario,
                                          motivo = equipoMantenimiento.motivo.trim(),
                                          estado = equipoMantenimiento.estado.trim(),
                                          fechaEntrada = Date.valueOf(LocalDate.now()),
                                          fechaSalida = equipoMantenimiento.fechaSalida,
                                          fechaProximoMantenimiento = equipoMantenimiento.fechaProximoMantenimiento)
        )
    }

    fun findAllEquiposMantenimiento(): List<equiposMantenimientoView> =equipoMantenimientoRepository.findAllView()

    fun findByIDEquipoMantenimiento(IDEquipoMantenimiento: Int) = equipoMantenimientoRepository.findById(IDEquipoMantenimiento)
        .orElseThrow { IllegalArgumentException("El equipo en mantenimiento no existe") }

    fun updateEquipoMantenimiento(dto: equipoMantenimientoDTO) {
        val equipoMantenimiento = findByIDEquipoMantenimiento(dto.IDEquipoMantenimiento)
        val articuloEnMantenimiento = equipoMantenimientoRepository.findById(dto.IDEquipoMantenimiento)
            .orElseThrow { IllegalArgumentException("No existe el equipo en mantenimiento") }
        val articulo = articuloParticularRepository.findById(dto.numeroSerie)
            .orElseThrow { IllegalArgumentException("El articulo no existe") }
        val usuario = usuariosRepository.findById(dto.numeroControl)
            .orElseThrow { IllegalArgumentException("El usuario no existe") }

        if (dto.fechaEntrada != articuloEnMantenimiento.fechaEntrada ||
            dto.fechaSalida != articuloEnMantenimiento.fechaSalida ||
            dto.fechaProximoMantenimiento != articuloEnMantenimiento.fechaProximoMantenimiento) {
            require(dto.fechaEntrada < dto.fechaSalida)
            { "La fecha de entrada no puede ser después de la fecha de salida" }
            require(dto.fechaProximoMantenimiento > dto.fechaEntrada)
            { "La fecha del próximo mantenimiento no puede ser antes de la fecha de entrada" }
            require(dto.fechaProximoMantenimiento > dto.fechaSalida)
            { "La fecha del próximo mantenimiento no puede ser antes de la salida" }
            require(dto.fechaProximoMantenimiento > articulo.fechaUltimoMantenimiento)
            { "La fecha del próximo mantenimiento no puede ser antes del último mantenimiento" }
        }

        when(dto.estado){
            "Finalizado" -> {
                require(LocalDate.now() >= equipoMantenimiento.fechaSalida.toLocalDate())
                { "Para poder marcar un articulo como finalizado, la fecha de hoy debe ser despues de la fecha de salida" }
                articulo.estado = "Disponible"
                val expediente = expedienteArticulo(
                    numeroSerie = articulo,
                    numeroControl = usuario,
                    fechaEntrada = dto.fechaEntrada,
                    fechaSalida = dto.fechaSalida
                )
                expedienteMantenimientoRepository.save(expediente)
                articuloParticularRepository.save(articulo)
                if(dto.estadoArticulo == "Disponible"){
                    articuloEnMantenimiento.apply {
                        equipo = articulo
                        personalEncargado = usuario
                        motivo = dto.motivo
                        estado = dto.estadoArticulo
                        fechaEntrada = dto.fechaEntrada
                        fechaSalida = dto.fechaSalida
                        fechaProximoMantenimiento = dto.fechaProximoMantenimiento
                    }
                    equipoMantenimientoRepository.save(articuloEnMantenimiento)
                    equipoMantenimientoRepository.delete(equipoMantenimiento)
                }else{
                    val articulosConEseModelo = articuloParticularRepository.countByModelo_Modelo(articulo.modelo!!.modelo)
                    val expedientes = expedienteMantenimientoRepository.findByNumeroSerie_NumeroSerie(articulo.numeroSerie)
                    articuloParticularRepository.delete(articulo)
                    expedienteMantenimientoRepository.deleteAll(expedientes)
                    equipoMantenimientoRepository.delete(equipoMantenimiento)
                    if(articulosConEseModelo <= 1){ modeloGeneralRepository.deleteById(articulo.modelo!!.modelo) }
                }
            }
            "Cancelado" -> {
                articulo.estado= "Disponible"
                articuloParticularRepository.save(articulo)
                equipoMantenimientoRepository.delete(equipoMantenimiento)
            }
        }
    }
}