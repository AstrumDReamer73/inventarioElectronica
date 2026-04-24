package com.example.inventarioElectronica.Exporters

import com.example.inventarioElectronica.Views.usuarioView
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import jakarta.servlet.http.HttpServletResponse

class usuariosExporterPDF(private val listaUsuarios: List<usuarioView>) {
    fun escribirCabecera(tabla: PdfPTable){
        val celda = PdfPCell()
        celda.backgroundColor= BaseColor.WHITE
        celda.setPadding(5f)
        celda.horizontalAlignment = Element.ALIGN_CENTER

        val fuente= FontFactory.getFont(FontFactory.HELVETICA_BOLD)
        fuente.color= BaseColor.BLACK
        fuente.size = 9f

        val headers=listOf("Numero de control","Nombre","Correo","Telefono","Rol")
        for(header in headers){
            celda.phrase = Phrase(header,fuente)
            tabla.addCell(celda)
        }
    }

    fun escribirDatos(tabla: PdfPTable){
        val fuente = FontFactory.getFont(FontFactory.HELVETICA)
        fuente.size=8f

        for(user in listaUsuarios){
            tabla.addCell(Phrase(user.numeroControl,fuente))
            tabla.addCell(Phrase(user.nombre,fuente))
            tabla.addCell(Phrase(user.correo,fuente))
            tabla.addCell(Phrase(user.telefono,fuente))
            tabla.addCell(Phrase(user.rol,fuente))
        }
    }

    fun exportar(response: HttpServletResponse){
        val documento = Document(PageSize.A4.rotate())
        PdfWriter.getInstance(documento,response.outputStream)
        documento.open()

        val fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD)
        fuenteTitulo.size=24f
        fuenteTitulo.color= BaseColor.BLACK

        val titulo = Paragraph("Reporte de usuarios",fuenteTitulo)
        titulo.alignment= Paragraph.ALIGN_CENTER
        titulo.spacingAfter=15f
        documento.add(titulo)

        val tabla= PdfPTable(5)
        tabla.setWidthPercentage(100f)
        tabla.setSpacingBefore(10f)
        tabla.isSplitLate = false
        tabla.isSplitRows = true

        tabla.setWidths(floatArrayOf(5f,5f,5f,5f,5f))
        escribirCabecera(tabla)
        escribirDatos(tabla)
        documento.add(tabla)
        documento.close()
    }
}