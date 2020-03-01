package com.raritasolutions.mymining.converter

import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.model.converter.word.WordDocumentTable
import com.raritasolutions.mymining.model.converter.word.WordTableCell
import com.raritasolutions.mymining.utils.groupRowRegex
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.slf4j.LoggerFactory
import java.io.InputStream

private val logger = LoggerFactory.getLogger(RebornWordConverter::class.java)

class RebornWordConverter : RebornConverter() {

    override var report: ExtractionReport? = null

    override fun convert(data: InputStream, defaultBuilding: Int): List<RawPairRecord> {
        val document = XWPFDocument(data)
        val tables = document.tables

        if (tables.size > 1)
            logger.warn("Unusual situation in $document: More than 1 table in the document (${tables.size})")

        val scheduleTable = tables[0]
        val docTable = WordDocumentTable(tables[0])

        val rowIterator = scheduleTable.rows
                .asSequence()
                .dropWhile { !it.getCell(0).text.matches(groupRowRegex) }
                .iterator()

        val groups = rowIterator.next()
                .tableCells
                .map { it.text }
                .asSequence()
                //.makeGroupsMap()

        fun getCellBorders(i: Int, j: Int): String {
            val target = WordTableCell(scheduleTable.getRow(i).getCell(j), i, j)
            return "Coords: $i, $j\nBorders: Top ${target.hasTopBorder()} Bottom ${target.hasBottomBorder()} Left ${target.hasLeftBorder()} Right ${target.hasRightBorder()}"
        }

        val cellICanDoSomethingWith = rowIterator.next()
                .getCell(3)

        val timeRecords = "something lol"

        print(timeRecords)
        return emptyList()
    }

}