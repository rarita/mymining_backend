package com.raritasolutions.mymining.model.converter

/**
 * Universal interface representing either a Word document table
 * or Excel Spreadsheet
 */
interface DocumentTable {

    // I don't really care about row personal properties
    // So I might just represent them as List of TableCells
    val rows: Sequence<Sequence<TableCell>>

    operator fun get(i: Int, j: Int): TableCell

    fun getGroupsMap(): Map<Int, String>

    fun composeContents(firstRow: Int,
                        lastRow: Int,
                        firstColumn: Int,
                        lastColumn: Int): FormattedString

}
