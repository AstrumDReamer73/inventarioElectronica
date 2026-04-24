package com.example.inventarioElectronica.Exporters

import com.example.inventarioElectronica.Views.equiposMantenimientoView
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.Date

class mantenimientoExporterExcel(private val listaArticulos: List<equiposMantenimientoView>) {
    fun exportar(response: HttpServletResponse){
        val workbook= XSSFWorkbook()
        val sheet = workbook.createSheet()

        val fontData = workbook.createFont().apply { fontHeightInPoints = 14 }

        val fontHeader = workbook.createFont().apply {
            fontHeightInPoints = 14
            bold = true
        }

        val headerStyle = workbook.createCellStyle().apply {
            setFont(fontHeader)
            alignment= HorizontalAlignment.JUSTIFY
            verticalAlignment= VerticalAlignment.CENTER
        }

        val dataStyle = workbook.createCellStyle().apply {
            setFont(fontData)
            alignment = HorizontalAlignment.JUSTIFY
            verticalAlignment = VerticalAlignment.CENTER
            dataFormat = workbook.creationHelper
                .createDataFormat()
                .getFormat("dd/MM/YYYY")
        }

        val wrapStyle = workbook.createCellStyle().apply {
            setFont(fontData)
            wrapText = true
            alignment = HorizontalAlignment.JUSTIFY
            verticalAlignment = VerticalAlignment.CENTER
        }

        val dateStyle = workbook.createCellStyle().apply {
            setFont(fontData)
            alignment = HorizontalAlignment.JUSTIFY
            verticalAlignment = VerticalAlignment.CENTER
            dataFormat = workbook.creationHelper
                .createDataFormat()
                .getFormat("dd/MM/yyyy")
        }

        fun setCell(row: Row, col:Int, value: Any?, style: CellStyle){
            val cell = row.createCell(col)
            when(value){
                is String -> cell.setCellValue(value)
                is Number -> cell.setCellValue(value.toDouble())
                is Date -> cell.setCellValue(value)
                else -> cell.setCellValue(value?.toString() ?: "")
            }
            cell.cellStyle = style
        }

        val header= sheet.createRow(0)
        val headers = listOf(
            "Numero de serie", "Modelo", "Descripcion",
            "Marca", "Motivo","Nombre del personal encargado",
            "Fecha de entrada","Fecha de salida","Estado"
        )

        headers.forEachIndexed { i,text -> setCell(header, i, text, headerStyle) }

        var rowNumber = 1
        for(art in listaArticulos){
            val row = sheet.createRow(rowNumber++)
            setCell(row,0,art.numeroSerie, dataStyle)
            setCell(row,1,art.modelo, dataStyle)
            setCell(row,2,art.descripcion, wrapStyle)
            setCell(row,3,art.marca, dataStyle)
            setCell(row,4,art.motivo, dataStyle)
            setCell(row,5,art.estado, dataStyle)
            setCell(row,6,art.fechaEntrada, dataStyle)
            setCell(row,7,art.fechaSalida, dateStyle)
            setCell(row,8,art.estado, dateStyle)
        }

        sheet.setColumnWidth(2, 8000)
        for(i in 0..8){
            if(i != 2){
                sheet.autoSizeColumn(i)
                val currentWidth = sheet.getColumnWidth(i)
                sheet.setColumnWidth(i, currentWidth + 1000)
            }
        }

        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        response.setHeader("Content-Disposition", "attachment; filename=Articulos en mantenimiento.xlsx")
        workbook.write(response.outputStream)
        workbook.close()
    }
}