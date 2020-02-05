package com.raritasolutions.mymining.model.converter

import org.apache.poi.ss.util.CellRangeAddress

class PseudoMergedRange(rowStart: Int, rowEnd: Int, cellStart: Int, cellEnd: Int, val contents: FormattedString)
    : CellRangeAddress(rowStart, rowEnd, cellStart, cellEnd), Comparable<PseudoMergedRange> {

    constructor(cra: CellRangeAddress, contents: FormattedString)
            : this(cra.firstRow, cra.lastRow, cra.firstColumn, cra.lastColumn, contents)

    operator fun contains(cell: TableCell): Boolean
            = (cell.rowIndex in firstRow..lastRow) && (cell.columnIndex in firstColumn..lastColumn)

    /** The more cell is close to top-left corner of the sheet
     *  the bigger it is considered to be in comparison
     *  **************************************************
     *  At first i wanted to implement this outside of the class in standalone Comparator
     *  However, Comparator appears to not affect behavior of .contains() and .remove()
     *  Since class does not implement Comparable interface and that is why it is here
     */
    override fun compareTo(other: PseudoMergedRange): Int
            = (this.firstRow - other.firstRow) + (this.firstColumn - other.firstColumn)

}