package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.utils.groupRegex
import com.raritasolutions.mymining.utils.shrink
import org.apache.commons.csv.CSVFormat
import java.io.Reader

// Note that source MUST BE in UTF-8 charset.
fun getRawListFromCSV(source: Reader): List<RawPairRecord> {

    var dayKey: String? = "День недели"
    var timeKey = "Время | Группа"

    val csvFormat = CSVFormat
            .DEFAULT
            .withFirstRecordAsHeader()
            .withRecordSeparator('\n')
            .withTrim()

    val csvContents = csvFormat.parse(source)

    val groupsList = csvContents.headerMap
            .map {it.key}
            .filter { groupRegex.matches(it) }

    val rawList = ArrayList<RawPairRecord>()

    var currentDay = "N/A"
    var currentTime: String
    csvContents.records.forEach {
        currentTime = it[timeKey]
        if (dayKey != null) {
            if (it[dayKey].isNotBlank())
                currentDay = it[dayKey].shrink()

            if (currentDay == "пятница") {
                timeKey = dayKey!!
                dayKey = null
            }
        }
        // One should not use second forEach to avoid reference hell
            for (group in groupsList)
                rawList.add(RawPairRecord(
                        currentDay,
                        currentTime,
                        group,
                        it[group]))
            }
    return rawList
}
