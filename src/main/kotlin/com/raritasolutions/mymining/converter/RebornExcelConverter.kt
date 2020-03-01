package com.raritasolutions.mymining.converter

import com.raritasolutions.mymining.model.BuildingData
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.model.converter.excel.ExcelDocumentTable
import com.raritasolutions.mymining.utils.BUILDINGS_CELL_COLOR
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.InputStream

@Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
@Component("reborn")
class RebornExcelConverter : RebornConverter() {

    override var report: ExtractionReport? = null

    /* XLS-s extracted by SimplePDF are accepted by this method */
    override fun convert(data: InputStream, defaultBuilding: Int): List<RawPairRecord> {
        val workbook = XSSFWorkbook(data)

        val scheduleSheet = ExcelDocumentTable(workbook.getSheetAt(0))
        // Iterate through table
        // Нормальный код
        val pseudoMergedCells = scheduleSheet.findPseudoMergedCells()

        val groups = scheduleSheet.getGroupsMap()
        val timeRecords = scheduleSheet.makeTimesMap(pseudoMergedCells)
        val rawPairList = arrayListOf<RawPairRecord>()
        /* Iterate through columns containing time
         * For each column iterate over groups */
        for (timeRecord in timeRecords) {
            for (timeItem in timeRecord.timeWithTLP) {
                for (groupItem in groups) {
                    val cell = scheduleSheet[timeItem.key, groupItem.key]
                    val cellContents =
                        if (pseudoMergedCells.any {cell in it}) // If it is part of merged range
                            pseudoMergedCells.first { cell in it }.contents
                        else
                            cell.richString

                    if (cellContents.string.isNotBlank()) {
                        // If cell itself has BG color, paint all text in this color
                        // Otherwise, analyse the contents for possible colored text
                        val buildingData =
                            if (!cell.isDefaultColor()) {
                                val cellColor = cell.getColor()
                                listOf(BuildingData(0, cellColor.findBuildingId(BUILDINGS_CELL_COLOR)
                                        ?: throw IllegalStateException("Color $cellColor is not present in $BUILDINGS_CELL_COLOR")))
                            }
                            else
                                cell.richString.toBuildingData(defaultBuilding)


                        rawPairList += RawPairRecord(
                            timeRecord.dayIndex,
                            timeItem.value,
                            groupItem.value,
                            cellContents.string,
                            buildingData
                        )
                    }
                }
            }
        }
        data.close()
        return rawPairList
    }
}