package com.example.inventarioElectronica.Exporters

import com.example.inventarioElectronica.Views.usuarioView
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class usuariosExporterExcel(private val listaUsuarios: List<usuarioView>) {
    fun exportar(response: HttpServletResponse){
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Usuarios")

        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("Numero de control")
        header.createCell(1).setCellValue("Nombre")
        header.createCell(2).setCellValue("Correo")
        header.createCell(3).setCellValue("Telefono")
        header.createCell(4).setCellValue("Rol")

        var rowNumber = 1
        for (user in listaUsuarios){
            val row = sheet.createRow(rowNumber++)
            row.createCell(0).setCellValue(user.numeroControl)
            row.createCell(1).setCellValue(user.nombre)
            row.createCell(2).setCellValue(user.correo)
            row.createCell(3).setCellValue(user.telefono)
            row.createCell(4).setCellValue(user.rol)
        }

        for(i in 0..4){
            sheet.autoSizeColumn(i)
            val currentWidth=sheet.getColumnWidth(i)
            sheet.setColumnWidth(i,currentWidth+1000)
        }

        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        response.setHeader("Content-Disposition","attachment; filename=usuarios.xlsx")
        workbook.write(response.outputStream)
        workbook.close()
    }
}