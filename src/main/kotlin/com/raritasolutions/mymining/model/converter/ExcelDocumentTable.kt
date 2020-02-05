package com.raritasolutions.mymining.model.converter

import com.raritasolutions.mymining.utils.groupRowRegex
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFSheet

class ExcelDocumentTable(private val source: XSSFSheet): DocumentTable {

    override val rows: Sequence<Sequence<TableCell>>
        get() = source.rowIterator()
                .asSequence()
                .map { it.asSequence().map { ExcelTableCell(it as XSSFCell) } }

    override fun getGroupsMap(): Map<Int, String> {
        val rowIterator
                = source // Head to the groups row
                .rowIterator()
                .asSequence()
                .dropWhile { !it.getCell(0).stringCellValue.matches(groupRowRegex) }
                .iterator()

        return rowIterator.next()
                .asSequence()
                .map(Cell::getStringCellValue)
                .makeGroupsMap()
    }

    // TODO this can be done easier
    private fun Sequence<String>.makeGroupsMap(): Map<Int, String> {
        // Find the groups line
        return this
                .drop(2)
                .mapIndexed {index, cellText -> Pair(index + 2, cellText) } // Index + 2 since we've dropped first two
                .filter { it.second.isNotBlank() }
                .toMap()
    }

    override fun get(i: Int, j: Int): TableCell
        = ExcelTableCell(source.getRow(i).getCell(j))

    // Maybe it should be made more generic than it is now
    // But I also hate seeing useless methods in the [FormattedString] class
    override fun composeContents(firstRow: Int,
                                 lastRow: Int,
                                 firstColumn: Int,
                                 lastColumn: Int) : FormattedString {

        val accumulator = ExcelFormattedString(XSSFRichTextString(""))
        for (row in firstRow..lastRow)
            for (column in firstColumn..lastColumn) {
                accumulator.append(this[row, column].richString as ExcelFormattedString)
                if (!(row == lastRow && column == lastColumn))
                    accumulator.append(" ")
            }
        return accumulator

    }
}