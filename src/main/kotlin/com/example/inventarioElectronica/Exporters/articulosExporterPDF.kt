package com.example.inventarioElectronica.Exporters

import com.example.inventarioElectronica.DTO.articuloDTO
import com.example.inventarioElectronica.Views.articulosView
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import jakarta.servlet.http.HttpServletResponse
import kotlin.toString

class articulosExporterPDF(private val listaArticulos: List<articulosView>) {
    fun exportar(response: HttpServletResponse) {
        val documento = Document(PageSize.A3.rotate())
        PdfWriter.getInstance(documento, response.outputStream)
        documento.open()
        val fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD).apply {
            size = 20f
            color = BaseColor.BLACK
        }

        val titulo = Paragraph("Reporte de articulos", fuenteTitulo).apply {
            alignment = Paragraph.ALIGN_CENTER
            spacingAfter = 15f
        }

        documento.add(titulo)

        val tabla = PdfPTable(11).apply {
            widthPercentage = 100f
            setSpacingBefore(10f)
            isSplitLate = false
            isSplitRows = true
            setWidths(FloatArray(11) { 3f })
        }

        val fuenteHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD).apply {
            size = 10f
            color = BaseColor.BLACK
        }

        val fuenteData = FontFactory.getFont(FontFactory.HELVETICA).apply {
            size = 9f
            color = BaseColor.BLACK
        }

        fun setCell(texto: String, fuente: Font): PdfPCell {
            return PdfPCell(Phrase(texto, fuente)).apply {
                setPadding(5f)
                horizontalAlignment = Element.ALIGN_LEFT
                verticalAlignment = Element.ALIGN_MIDDLE
            }
        }

        val headers = listOf(
            "Numero de serie","Modelo","Descripcion","Categoria","Marca",
            "Estado","Plazo de garantia","Fecha de Ingreso",
            "Fecha de ultimo mantenimiento","Fecha de proximo mantenimiento","Ubicacion"
        )

        headers.forEach { tabla.addCell(setCell(it, fuenteHeader)) }
        for (art in listaArticulos) {
            tabla.addCell(setCell(art.numeroSerie, fuenteData))
            tabla.addCell(setCell(art.modelo, fuenteData))
            tabla.addCell(setCell(art.descripcion, fuenteData))
            tabla.addCell(setCell(art.categoria, fuenteData))
            tabla.addCell(setCell(art.marca, fuenteData))
            tabla.addCell(setCell(art.estado, fuenteData))
            tabla.addCell(setCell(art.plazoGarantia, fuenteData))
            tabla.addCell(setCell(art.fechaIngreso.toString(), fuenteData))
            tabla.addCell(setCell(art.fechaUltimoMantenimiento.toString(), fuenteData))
            tabla.addCell(setCell(art.fechaProximoMantenimiento.toString(), fuenteData))
            tabla.addCell(setCell(art.ubicacion, fuenteData))
        }
        documento.add(tabla)
        documento.close()
    }
}