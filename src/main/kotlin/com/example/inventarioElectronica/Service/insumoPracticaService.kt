package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.DTO.insumoDTO
import com.example.inventarioElectronica.Model.insumoPractica
import com.example.inventarioElectronica.Repository.articuloParticularRepository
import com.example.inventarioElectronica.Repository.asignacionRepository
import com.example.inventarioElectronica.Repository.insumosRepository
import com.example.inventarioElectronica.Views.insumosView
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.sql.Date
import java.time.LocalDate

@Service @Transactional class insumoPracticaService(
    private val insumosRepository: insumosRepository,
    private val articulosRepository: articuloParticularRepository,
    private val asignacionRepository: asignacionRepository
) {
    fun saveInsumos(asignacion: Int, insumos: List<insumoDTO>) {
        if (insumos.isEmpty()) return
        val asignacion = asignacionRepository.findById(asignacion)
            .orElseThrow { IllegalArgumentException("La IDAsignacion no existe") }
        val nuevos = insumos.filter { it.numeroSerie.isNotBlank() }
            .distinctBy { it.numeroSerie }
            .map {
                val articulo = articulosRepository.findById(it.numeroSerie)
                    .orElseThrow { IllegalArgumentException("Articulo no existe") }
                articulo.estado="Reservado"
                insumoPractica(articulo=articulo,
                               IDAsignacion = asignacion,
                               estado = it.estado.ifBlank { "Disponible" })
            }
        insumosRepository.saveAll(nuevos)
    }

    fun findInsumo(search: String) = insumosRepository.findInsumo(search)

    fun findByAsignacion(IDAsignacion:Int):List<insumosView> {
        val insumos = insumosRepository.findByIDAsignacion_IDAsignacion(IDAsignacion)
        return insumos.map {
            val articulo = it.articulo!!
            val modelo = articulo.modelo!!
            insumosView(modelo = modelo.modelo,
                        descripcion = modelo.descripcion,
                        categoria = modelo.categoria,
                        marca = modelo.marca,
                        numeroSerie = articulo.numeroSerie,
                        fechaUltimoMantenimiento = articulo.fechaUltimoMantenimiento!!.toLocalDate(),
                        estado = articulo.estado,
                        ubicacion = articulo.ubicacion)
        }
    }

    fun findAll() = insumosRepository.findAll()

    fun findAllArticulosElegibles(fechaUso: LocalDate, insumosAgregados:List<String> = emptyList()) =
        insumosRepository.findArticulosElegibles(Date.valueOf(fechaUso.minusWeeks(2)), Date.valueOf(fechaUso.plusWeeks(2))).filter{ it.articulo!!.numeroSerie !in insumosAgregados}
}