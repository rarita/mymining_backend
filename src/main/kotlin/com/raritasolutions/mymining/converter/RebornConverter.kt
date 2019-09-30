package com.raritasolutions.mymining.converter

import com.raritasolutions.mymining.model.BuildingData
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.model.reborn.DayTimeRecord
import com.raritasolutions.mymining.model.reborn.PseudoMergedRange
import com.raritasolutions.mymining.model.reborn.RGBColor
import com.raritasolutions.mymining.utils.*
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.InputStream

@Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
@Component("reborn")
class RebornConverter : BaseConverter {

    override var report: ExtractionReport? = null

    // Include trailing and leading spaces just in case
    private val timeRegex = "\\s*\\d+\\.\\d{2,}-\\d{2,}\\.\\d{2,}\\s*".toRegex()
    private val groupRowRegex = "День\\s?недели".toRegex()

    private fun Row.makeGroupsMap(): Map<Int, String> {
        // Find the groups line
                return this
                    .iterator()
                    .asSequence()
                    .drop(2)
                    .mapIndexed {index, cell -> Pair(index + 2, cell.stringCellValue)} // Index + 2 since we've dropped first two
                    .filter { it.second.isNotBlank() }
                    .toMap()
    }

    private fun XSSFSheet.makeTimesMap(pseudoMergedCells : List<PseudoMergedRange>): List<DayTimeRecord> {
        val days = pseudoMergedCells
            .filter { it.firstColumn == 0 && it.lastColumn == 0}
        val dayTimeRecords = arrayListOf<DayTimeRecord>()
        for (day in days) {
            // Find every time cell corresponding to this day
            val times = sortedSetOf<PseudoMergedRange>()
            for (row in day.firstRow..day.lastRow) {
                val cell = this.getRow(row).getCell(1)
                times += if (cell.isBorderComplete())
                    PseudoMergedRange(cell.toCellRangeAddress(), cell.richStringCellValue)
                else
                    pseudoMergedCells.first { it.contains(cell) } // Should be only one anyway
            }
            /* Check if all the time tags are correct
            *  If some are missing, fill them regarding their position on the sheet */
            val brokenTimes = times.filter { !it.contents.string.matches(timeRegex) }
            for (time in brokenTimes) {
                val donor = times.firstOrNull { it.contents.string.matches(timeRegex) }
                        ?: throw IllegalStateException("Bad input: All time records seem to be broken @${day.contents.string}")
                val correctTimeIndex
                        = times.indexOf(time) - times.indexOf(donor) + TIMES_LIST.indexOf(donor.contents.string.trim())
                val correctTime = TIMES_LIST[correctTimeIndex]
                times -= time
                times += PseudoMergedRange(time, XSSFRichTextString(correctTime))
            }

            dayTimeRecords += DayTimeRecord(days.indexOf(day) + 1,
                times.associate { it.firstRow to it.contents.string })
        }
        return dayTimeRecords
    }

    private fun XSSFSheet.findPseudoMergedCells(): List<PseudoMergedRange> {
        // Search for pseudo-merged cells (i.e. the ones that not merged but were meant to)
        // We'll need to analyse borders of the non-complete cells

        val borderMask = arrayListOf<MutableList<Boolean>>()
        for (row in this.iterator())
            borderMask += row.asSequence().map { it.isBorderComplete() }.toMutableList()

        val pseudoMerged = arrayListOf<PseudoMergedRange>()
        for (rowIndex in 0 until borderMask.size) {
            for (cellIndex in 0 until borderMask[0].size) { // Since borderMask is MxN matrix we can do this
                // Try to expand right then down
                // First found cell should be top left. Check just in case.
                if (!borderMask[rowIndex][cellIndex] && this[rowIndex, cellIndex].isStarting()) {
                    var offsetRight = 0
                    while (this[rowIndex, cellIndex + offsetRight].cellStyle.borderRight < 1) offsetRight++
                    var offsetBottom = 0
                    while (this[rowIndex + offsetBottom, cellIndex + offsetRight].cellStyle.borderBottom < 1) offsetBottom++
                    // Compose merged cell contents
                    val contents
                            = this.composeContents(rowIndex, rowIndex + offsetBottom, cellIndex, cellIndex + offsetRight)
                    pseudoMerged += PseudoMergedRange(rowIndex, rowIndex + offsetBottom, cellIndex, cellIndex + offsetRight, contents)
                    // Mark this cells group processed to avoid unwanted intersected cells
                    for (row in rowIndex..rowIndex + offsetBottom)
                        for (cell in cellIndex..cellIndex + offsetRight)
                            borderMask[row][cell] = true
                }
            }
        }
        return pseudoMerged
    }

    private fun RGBColor.findBuildingId(buildingColors: Map<RGBColor, Int>)
            = buildingColors.entries.firstOrNull { this == it.key }?.value

    private fun XSSFRichTextString.toBuildingData(defaultBuilding: Int): List<BuildingData>? {
        val formatting = mutableListOf<BuildingData>()
        var fontStartIndex = 0
        for (runIndex in 0 until this.numFormattingRuns()) {
            // Skip iteration if formatting run has zero length bc trying to get the font causes NPE
            if (this.getLengthOfFormattingRun(runIndex) == 0)
                continue
            val font = this.getFontOfFormattingRun(runIndex)
            val currentBuilding=
                if (font.xssfColor != null && !font.xssfColor.isAuto)
                    RGBColor(font.xssfColor.rgb).findBuildingId(BUILDINGS_TEXT_COLOR)
                else
                    defaultBuilding
            // If the color does not represent building, skip the iteration
            if (currentBuilding == null)
                continue
            // Check if previous run was of the same color
            // If it wasn't, add new ColorFormatting to the list
            if (formatting.isNotEmpty() && formatting.last().buildingId != currentBuilding)
                formatting += BuildingData(fontStartIndex, currentBuilding)
            else if (formatting.isEmpty())
                formatting += BuildingData(fontStartIndex, currentBuilding)

            fontStartIndex += this.getLengthOfFormattingRun(runIndex)
        }
        return if ((formatting.size > 1) && formatting.first().buildingId != defaultBuilding)
            formatting
        else
            null
    }

    /* XLS-s extracted by SimplePDF are accepted by this method */
    override fun convert(data: InputStream, defaultBuilding: Int): List<RawPairRecord> {
        val workbook = XSSFWorkbook(data)
        val scheduleSheet = workbook.getSheetAt(0)
        // Iterate through table
        // Нормальный код
        val pseudoMergedCells = scheduleSheet.findPseudoMergedCells()
        val rowIterator
                = scheduleSheet // Head to the groups row
                      .rowIterator()
                      .asSequence()
                      .dropWhile { !it.getCell(0).stringCellValue.matches(groupRowRegex) }
                      .iterator()
        val groups = rowIterator.next().makeGroupsMap()
        val timeRecords = scheduleSheet.makeTimesMap(pseudoMergedCells)
        val rawPairList = arrayListOf<RawPairRecord>()
        /* Iterate through columns containing time
         * For each column iterate over groups */
        for (timeRecord in timeRecords) {
            for (timeItem in timeRecord.timeWithTLP) {
                for (groupItem in groups) {
                    val cell = scheduleSheet
                        .getRow(timeItem.key)
                        .getCell(groupItem.key)
                    val cellContents =
                        if (pseudoMergedCells.any {cell in it}) // If it is part of merged range
                            pseudoMergedCells.first { cell in it }.contents
                        else
                            cell.richStringCellValue

                    if (cellContents.string.isNotBlank()) {
                        // If cell itself has BG color, paint all text in this color
                        // Otherwise, analyse the contents for possible colored text
                        val buildingData =
                            if (cell.cellStyle.fillForegroundXSSFColor != null && !cell.cellStyle.fillForegroundXSSFColor.isAuto) {
                                val cellColor = RGBColor(cell.cellStyle.fillForegroundXSSFColor.rgb)
                                listOf(BuildingData(0, cellColor.findBuildingId(BUILDINGS_CELL_COLOR)
                                        ?: throw IllegalStateException("Color $this is not present in $BUILDINGS_CELL_COLOR")))
                            }
                            else
                                cell.richStringCellValue.toBuildingData(defaultBuilding)


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