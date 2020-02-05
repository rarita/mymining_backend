package com.raritasolutions.mymining.utils

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFSheet

/**
 * [] wrapper for XSSFSheet
 */
operator fun XSSFSheet.get(i: Int, j: Int)
        = this.getRow(i).getCell(j) ?: throw IllegalStateException("Cell @ $i,$j is null")

/**
 *  Removes all leading and trailing line breaks and/or spaces from given RichTextString
 */
fun XSSFRichTextString.normalize() {
    if (this.string.trim() == this.string)
        return
}

/**
 *  Checks whether the cell starts corresponding PseudoMergedRange
 *  (top and left borders are present, bottom and right aren't)
 */
fun Cell.isStarting() : Boolean
        = with(this.cellStyle) { (borderLeft.code * borderTop.code == 1 || borderTop.code * (this@isStarting.columnIndex + 1) == 1)
        && borderRight.code * borderBottom.code == 0 }

/**
 *  Checks whether the cell starts the PseudoMergedRange
 *  The exact opposite of Cell.isStarting()
 */
fun Cell.isEnding() : Boolean
        = with(this.cellStyle) { borderRight.code * borderBottom.code == 1 && borderLeft.code * borderTop.code == 0 }