package com.raritasolutions.mymining.utils

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFSheet

/**
 * [] wrapper for XSSFSheet
 */
operator fun XSSFSheet.get(i: Int, j: Int)
        = this.getRow(i).getCell(j) ?: throw IllegalStateException("Cell @ $i,$j is null")

/**
 * Composes contents of a cell range into one RichTextString
 * Params are coordinates of the targeted cell range
 * @return RichTextString containing all contents from the given range
 */
fun XSSFSheet.composeContents(firstRow: Int,
                              lastRow: Int,
                              firstColumn: Int,
                              lastColumn: Int): XSSFRichTextString
{
    val accumulator = XSSFRichTextString("")
    for (row in firstRow..lastRow)
        for (column in firstColumn..lastColumn) {
            accumulator.append(this[row, column].richStringCellValue)
            if (!(row == lastRow && column == lastColumn))
                accumulator.append(" ")
        }
    return accumulator
}

/**
 * Pretty self-explanatory method that adds one RichTextString to another
 * @param other: String that is going to be added
 */
fun XSSFRichTextString.append(other: XSSFRichTextString) {
    // Check for blank strings on input
    if (other.string.isBlank()) return
    // Do it with styles and we're good to go
    val formattingRuns = other.numFormattingRuns()
    for (run in 0 until formattingRuns) {
        val start = other.getIndexOfFormattingRun(run) // Start of the run
        val length = other.getLengthOfFormattingRun(run)
        val contents = other.string.slice(start until start + length)
        // Skip empty formatting runs
        if (contents.isEmpty())
            continue
        val style = other.getFontOfFormattingRun(run)
        // Append part of string that corresponds to the run to the receiver
        this.append(contents, style)
    }
}

/**
 *  Removes all leading and trailing line breaks and/or spaces from given RichTextString
 */
fun XSSFRichTextString.normalize() {
    if (this.string.trim() == this.string)
        return
}

/**
 *  Transforms the cell to CellRangeAddress that contains only this cell
 */
fun Cell.toCellRangeAddress(): CellRangeAddress
        = CellRangeAddress(this.rowIndex, this.rowIndex, this.columnIndex, this.columnIndex)

/**
 *  Checks whether the cell starts the PseudoMergedRange
 *  (top and left borders are present, bottom and right aren't)
 */
fun Cell.isStarting() : Boolean
        = with(this.cellStyle) { (borderLeft * borderTop == 1 || borderTop * (this@isStarting.columnIndex + 1) == 1)
        && borderRight * borderBottom == 0 }

/**
 *  Checks whether the cell starts the PseudoMergedRange
 *  The exact opposite of Cell.isStarting()
 */
fun Cell.isEnding() : Boolean
        = with(this.cellStyle) { borderRight * borderBottom == 1 && borderLeft * borderTop == 0 }

/**
 * Checks if the cell has all of the borders
 */
fun Cell.isBorderComplete() : Boolean
        = with(this.cellStyle) { borderBottom * borderTop * borderLeft * borderRight > 0 }