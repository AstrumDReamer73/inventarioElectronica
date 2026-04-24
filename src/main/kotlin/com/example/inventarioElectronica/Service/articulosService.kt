package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.DTO.articuloDTO
import com.example.inventarioElectronica.Model.articuloParticular
import com.example.inventarioElectronica.Model.modeloGeneral
import com.example.inventarioElectronica.Repository.articuloParticularRepository
import com.example.inventarioElectronica.Repository.expedienteArticuloRepository
import com.example.inventarioElectronica.Repository.modeloGeneralRepository
import com.example.inventarioElectronica.Views.articulosView
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service @Transactional class articulosService(
    private val modeloGeneralRepository: modeloGeneralRepository,
    private val articuloParticularRepository: articuloParticularRepository,
    private val expedienteMantenimientoRepository: expedienteArticuloRepository
) {
    fun saveArticulo(dto: articuloDTO) {
        if(articuloParticularRepository.existsById(dto.numeroSerie)){ throw IllegalArgumentException("Ya existe un articulo con el numero de serie ${dto.numeroSerie}") }
        else{
            val modelo = modeloGeneralRepository.findById(dto.modelo)
                .orElse(modeloGeneralRepository.save(
                    modeloGeneral(modelo = dto.modelo.trim(),
                                          descripcion = dto.descripcion.trim(),
                                          marca = dto.marca.trim(),
                                          categoria = dto.categoria.trim())
                )
            )
            articuloParticularRepository.save(
                articuloParticular(numeroSerie = dto.numeroSerie.trim(),
                                           ubicacion= dto.ubicacion.trim(),
                                           plazoGarantia = dto.plazoGarantia.trim(),
                                           estado = "Disponible",
                                           modelo = modelo,
                                           fechaIngreso = dto.fechaIngreso,
                                           fechaProximoMantenimiento = dto.fechaProximoMantenimiento,
                                           fechaUltimoMantenimiento = dto.fechaUltimoMantenimiento)
            )
        }
    }

    fun findAllModelos() = modeloGeneralRepository.findAll()

    fun findByModelo(modelo: String) = modeloGeneralRepository.findById(modelo).orElseThrow { IllegalArgumentException("El modelo no existe") }

    fun findAllCategorias() = modeloGeneralRepository.findAllCategorias()

    fun findAllArticulos():List<articulosView> = articuloParticularRepository.findAllView()

    fun findAllUbicaciones() = articuloParticularRepository.findAllUbicaciones()

    fun findByNumeroSerie(numeroSerie: String):articuloDTO{
        val articulo = articuloParticularRepository.findByNumeroSerie(numeroSerie)
        val modelo = modeloGeneralRepository.findById(articulo.modelo!!.modelo)
            .orElseThrow{ IllegalArgumentException("El modelo no fue encontrado") }
        return articuloDTO(modelo = modelo.modelo,
                           descripcion = modelo.descripcion,
                           marca = modelo. marca,
                           categoria = modelo.categoria,
                           numeroSerie = articulo.numeroSerie,
                           plazoGarantia = articulo.plazoGarantia,
                           estado = articulo.estado,
                           ubicacion = articulo.ubicacion,
                           fechaIngreso = articulo.fechaIngreso,
                           fechaUltimoMantenimiento = articulo.fechaUltimoMantenimiento,
                           fechaProximoMantenimiento = articulo.fechaProximoMantenimiento)
    }

    fun updateArticulo(dto: articuloDTO) {
        val articulo = articuloParticularRepository.findById(dto.numeroSerie)
            .orElseThrow { IllegalArgumentException("El articulo no existe") }
        val modelo = modeloGeneralRepository.findById(dto.modelo)
            .orElseThrow { IllegalArgumentException("El modelo no existe") }
        modelo.apply {
            descripcion = dto.descripcion.trim()
            marca = dto.marca.trim()
            categoria = dto.categoria.trim()
        }
        articulo.apply {
            this.modelo = modelo
            estado = dto.estado
            ubicacion = dto.ubicacion.trim()
        }
        modeloGeneralRepository.save(modelo)
        articuloParticularRepository.save(articulo)
    }

    fun hacerExpediente(numeroSerie: String) = expedienteMantenimientoRepository.findByNumeroSerie_NumeroSerie(numeroSerie)
}