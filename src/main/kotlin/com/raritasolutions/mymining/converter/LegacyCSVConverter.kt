package com.raritasolutions.mymining.converter

import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.utils.DAYS_ORDER_MAP
import com.raritasolutions.mymining.utils.groupRegex
import com.raritasolutions.mymining.utils.removeCaretReturns
import org.apache.commons.csv.CSVFormat
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.charset.Charset

@Component("legacycsv")
class LegacyCSVConverter : BaseConverter {

    override var report: ExtractionReport? = null
    // Accepts text-based files with parsed CSV contents.
    override fun convert(data: InputStream, defaultBuilding: Int): List<RawPairRecord> {

        var dayKey: String? = "День недели"
        var timeKey = "Время | Группа"

        val csvFormat = CSVFormat
                .DEFAULT
                .withFirstRecordAsHeader()
                .withRecordSeparator('\n')
                .withTrim()

        // Omit first line
        val reader = data
                .bufferedReader((Charset.forName("UTF-8")))
                .apply { readLine() }

        val csvContents =
                csvFormat.parse(reader)

        val groupsList = csvContents.headerMap
                .map {it.key}
                .filter { groupRegex.matches(it) }

        val rawList = ArrayList<RawPairRecord>()

        var currentDay = "N/A"
        var currentTime: String
        csvContents.records.forEach{
            currentTime = it[timeKey]
            if (dayKey != null) {
                if (it[dayKey].isNotBlank())
                    currentDay = it[dayKey].removeCaretReturns()

                if (currentDay == "ПЯТНИЦА") {
                    timeKey = dayKey!!
                    dayKey = null
                }
            }
            // One should not use second forEach to avoid reference hell
            for (group in groupsList)
                rawList.add(RawPairRecord(
                        DAYS_ORDER_MAP[currentDay]!!,
                        currentTime,
                        group,
                        it[group].replace('\r',' ')))
        }
        data.close()
        return rawList
    }
}