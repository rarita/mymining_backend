package com.raritasolutions.mymining.converter

import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.utils.DAYS_ORDER_MAP
import com.raritasolutions.mymining.utils.groupRegex
import com.raritasolutions.mymining.utils.removeCaretReturns
import com.raritasolutions.mymining.utils.timeSpanRegex
import org.apache.pdfbox.pdmodel.PDDocument
import org.springframework.stereotype.Component
import technology.tabula.ObjectExtractor
import technology.tabula.Table
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm
import java.io.File

@Component("tabula")
class TabulaConverter : BaseConverter {

    override var report: ExtractionReport? = null

    // Accepts PDF files
    override fun convert(localFile: File, defaultBuilding: Int): List<RawPairRecord> {
        val table = getTabulaTable(localFile)
        return decompose(table)
    }

    private fun getTabulaTable(file: File): Table {
        val document = PDDocument.load(file)
        val objExtractor = ObjectExtractor(document)
        val page = objExtractor.extract(1)
        val table = SpreadsheetExtractionAlgorithm().extract(page)
        document.close()
        objExtractor.close()
        // Very uncanny, but i have nothing to do with randomly spawned subtables
        if (table.size > 1) {
            report?.addMessage("Dropping excess content on file @${file.toURI().toURL()}")
                    ?: throw IllegalStateException("No Reporter Attached")
            table.drop(1)
                    .flatMap { it.rows }
                    .forEach {
                        for (cell in it)
                            report?.addMessage("Dropping cell ${cell.text}")
                                    ?: throw IllegalStateException("No Reporter Attached")
                    }
        }
        return table[0]
    }

    private fun isTime(s: String?)
        = if (s.isNullOrBlank()) false else timeSpanRegex.matches(s)

    private fun decompose(table: Table): List<RawPairRecord> {
        val contents = table.rows
        val groupsList = contents[1]
                .map {it.text}
                .filter { groupRegex.matches(it) }

        val rawList = ArrayList<RawPairRecord>()

        var currentDay = "N/A"
        contents.drop(2).forEach {
            val currentTime = if (isTime(it[0].text)) it[0].text else it[1].text
            if ((it[0].text.isNotBlank()) && (!isTime(it[0].text))) {
                currentDay = it[0].text.removeCaretReturns()
            }
            val redundantRows = if (!isTime(it[0].text)) 2 else 1
            it
                .drop(redundantRows)
                .zip(groupsList)
                .forEach { rawList.add(RawPairRecord(
                        DAYS_ORDER_MAP[currentDay]!!,
                        currentTime,
                        it.second,
                        it.first.text.replace('\r',' ')
                )) }

            }
            return rawList
        }



}

