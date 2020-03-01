package com.raritasolutions.mymining.dump

import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.utils.DAYS_NAMES_MAP
import com.raritasolutions.mymining.utils.TIMES_LIST
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream

/**
 * Tool used to create RawPairRecord dumps
 * and store it in the Excel file
 */
class RawDumpTool {

    fun dumpData(data: List<RawPairRecord>, defaultBuilding: Int, outputStream: OutputStream) {

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("data_dump")

        val groups = data
                .map { it.group }
                .filter { it.matches("(ИАС|ИСТ).*".toRegex()) }
                .toSet()

        /** Draw groups **/
        val groupsRow = sheet.createRow(0)
        for (group in groups.withIndex()) {
            val cell = groupsRow.createCell(2 + group.index, CellType.STRING)
            cell.setCellValue(group.value)
        }

        /** Draw times (yuck!) **/
        for (day in DAYS_NAMES_MAP) {
            for (time in TIMES_LIST.withIndex()) {

                val row = sheet.createRow(TIMES_LIST.size * (day.key - 1) + time.index + 1)

                if (time.index == 0) {
                    val dayCell = row.createCell(0, CellType.STRING)
                    dayCell.setCellValue(day.value)
                }

                val timeCell = row.createCell(1, CellType.STRING)
                timeCell.setCellValue(time.value)

                /**
                 * Draw pairs
                 */
                for (group in groups.withIndex()) {
                    val pairCell = row.createCell(2 + group.index, CellType.STRING)
                    val pairData = data.filter { it.day == day.key && it.timeSpan == time.value && it.group == group.value }
                    if (pairData.size > 1)
                        throw IllegalStateException("Pair ambiguity at ${time.value}, ${day.value}")
                    val pairContents = pairData.firstOrNull()?.contents ?: ""
                    pairCell.setCellValue(pairContents)
                }

            }
        }

        outputStream.use { workbook.write(it) }

    }

}