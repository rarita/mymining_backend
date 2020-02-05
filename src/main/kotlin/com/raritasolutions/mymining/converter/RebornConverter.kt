package com.raritasolutions.mymining.converter

import com.raritasolutions.mymining.model.BuildingData
import com.raritasolutions.mymining.model.converter.*
import com.raritasolutions.mymining.utils.BUILDINGS_TEXT_COLOR
import com.raritasolutions.mymining.utils.TIMES_LIST
import org.apache.poi.xssf.usermodel.XSSFRichTextString

abstract class RebornConverter: BaseConverter {

    // Include trailing and leading spaces just in case
    protected val timeRegex = "\\s*\\d+\\.\\d{2,}-\\d{2,}\\.\\d{2,}\\s*".toRegex()

    protected fun DocumentTable.makeTimesMap(pseudoMergedCells : List<PseudoMergedRange>): List<DayTimeRecord> {
        val days = pseudoMergedCells
                .filter { it.firstColumn == 0 && it.lastColumn == 0}
        val dayTimeRecords = arrayListOf<DayTimeRecord>()
        for (day in days) {
            // Find every time cell corresponding to this day
            val times = sortedSetOf<PseudoMergedRange>()
            for (row in day.firstRow..day.lastRow) {
                //val cell = this.getRow(row).getCell(1)
                val cell = this[row, 1] //this.rows[row][1]
                times += if (cell.isBorderComplete())
                    PseudoMergedRange(cell.toCellRangeAddress(), cell.richString)
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
                times += PseudoMergedRange(time, ExcelFormattedString(XSSFRichTextString(correctTime))) // I don't like it
            }

            dayTimeRecords += DayTimeRecord(days.indexOf(day) + 1,
                    times.associate { it.firstRow to it.contents.string })
        }
        return dayTimeRecords
    }

    protected fun FormattedString.toBuildingData(defaultBuilding: Int): List<BuildingData>? {
        val formatting = mutableListOf<BuildingData>()
        var fontStartIndex = 0
        for (runIndex in 0 until this.numFormattingRuns()) {
            // Skip iteration if formatting run has zero length bc trying to get the font causes NPE
            if (this.getLengthOfFormattingRun(runIndex) == 0)
                continue

            val currentBuilding =
                    if (!this.isDefaultColor(runIndex))
                        this.getColorOfFormattingRun(runIndex).findBuildingId(BUILDINGS_TEXT_COLOR)
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

    protected fun DocumentTable.findPseudoMergedCells(): List<PseudoMergedRange> {
        // Search for pseudo-merged cells (i.e. the ones that not merged but were meant to)
        // We'll need to analyse borders of the non-complete cells

        val borderMask = arrayListOf<MutableList<Boolean>>()
        for (row in this.rows)
            borderMask += row.map { it.isBorderComplete() }.toMutableList()

        val pseudoMerged = arrayListOf<PseudoMergedRange>()
        for (rowIndex in 0 until borderMask.size) {
            for (cellIndex in 0 until borderMask[0].size) { // Since borderMask is MxN matrix we can do this
                // Try to expand right then down
                // First found cell should be top left. Check just in case.
                if (!borderMask[rowIndex][cellIndex] && this[rowIndex, cellIndex].isStarting()) {
                    var offsetRight = 0
                    while (!this[rowIndex, cellIndex + offsetRight].hasRightBorder()) offsetRight++
                    var offsetBottom = 0
                    while (!this[rowIndex + offsetBottom, cellIndex + offsetRight].hasBottomBorder()) offsetBottom++
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

    protected fun RGBColor.findBuildingId(buildingColors: Map<RGBColor, Int>)
            = buildingColors.entries.firstOrNull { this == it.key }?.value


}