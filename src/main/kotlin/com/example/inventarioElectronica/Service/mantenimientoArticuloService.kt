package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.DTO.equipoMantenimientoDTO
import com.example.inventarioElectronica.Model.expedienteArticulo
import com.example.inventarioElectronica.Model.mantenimientoArticulo
import com.example.inventarioElectronica.Repository.articuloParticularRepository
import com.example.inventarioElectronica.Repository.mantenimientoArticuloRepository
import com.example.inventarioElectronica.Repository.expedienteArticuloRepository
import com.example.inventarioElectronica.Repository.usuarioRepository
import com.example.inventarioElectronica.Views.equiposMantenimientoView
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.require

@Service @Transactional class mantenimientoArticuloService(
    private val equipoMantenimientoRepository: mantenimientoArticuloRepository,
    private val articuloParticularRepository: articuloParticularRepository,
    private val expedienteMantenimientoRepository: expedienteArticuloRepository,
    private val usuariosRepository: usuarioRepository
) {
    fun saveEquipoMantenimiento(equipoMantenimiento: equipoMantenimientoDTO) {
        val articulo = articuloParticularRepository.findById(equipoMantenimiento.numeroSerie)
            .orElseThrow { IllegalArgumentException("El articulo no existe") }
        val usuario = usuariosRepository.findById(equipoMantenimiento.numeroControl)
            .orElseThrow { IllegalArgumentException("El usuario no existe") }
        if(articulo.estado.equals("En mantenimiento")){ throw IllegalArgumentException("El articulo ya esta en mantenimiento") }

        require(equipoMantenimiento.fechaEntrada >= LocalDate.now())
        { "La fecha de entrada no puede ser anterior a hoy" }
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
                                          fechaEntrada = LocalDate.now(),
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

        if (dto.fechaSalida != articuloEnMantenimiento.fechaSalida ||
            dto.fechaProximoMantenimiento != articuloEnMantenimiento.fechaProximoMantenimiento) {
            require(dto.fechaSalida >= articuloEnMantenimiento.fechaEntrada)
            { "La fecha de salida no puede ser anterior a la fecha de entrada" }
            require(dto.fechaProximoMantenimiento > dto.fechaSalida)
            { "La fecha del próximo mantenimiento debe ser posterior a la fecha de salida" }
            require(dto.fechaProximoMantenimiento > articulo.fechaUltimoMantenimiento)
            { "La fecha del próximo mantenimiento debe ser posterior al último mantenimiento" }
        }

        when(dto.estado){
            "Finalizado" -> {
                require(LocalDate.now() >= dto.fechaSalida)
                { "Para poder marcar un artículo como finalizado, la fecha de hoy debe ser igual o posterior a la fecha de salida" }
                if(dto.estadoArticulo=="Disponible"){
                    articulo.estado = "Disponible"
                    val expediente = expedienteArticulo(
                        numeroSerie = articulo,
                        numeroControl = usuario,
                        fechaEntrada = equipoMantenimiento.fechaEntrada,
                        fechaSalida = dto.fechaSalida
                    )
                    articuloParticularRepository.save(articulo)
                    expedienteMantenimientoRepository.save(expediente)
                }else{
                    articulo.estado="En proceso de eliminacion"
                    articulo.fechaProximoMantenimiento= LocalDate.of(1900,1,1)
                    articulo.fechaUltimoMantenimiento = equipoMantenimiento.fechaProximoMantenimiento
                    articuloParticularRepository.save(articulo)
                }
            }
            "Cancelado" -> {
                articulo.estado= "Disponible"
                articuloParticularRepository.save(articulo)
            }
        }
        equipoMantenimientoRepository.delete(equipoMantenimiento)
    }
}