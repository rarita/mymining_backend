package com.raritasolutions.mymining.fetcher

import com.raritasolutions.mymining.extractor.RawConverter
import com.raritasolutions.mymining.extractor.getRawListFromCSV
import com.raritasolutions.mymining.model.PairRecord
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.StringReader
import java.lang.IllegalStateException

fun String.removeFirstLine()
        = this.substringAfter("\n")

fun txtToPairRecordList(filename: String): List<PairRecord>
{
    val parsedTXT = ClassPathResource("/textdata/parsed.txt").inputStream
    val source = parsedTXT
            .bufferedReader()
            .readText()
            .replace("\r"," ")
            .removeFirstLine()
    val sourceReader = StringReader(source)
    val rawList = getRawListFromCSV(sourceReader)
    val extractorList = RawConverter(rawList).extractorList
    extractorList.forEach { it.make() }
    if (extractorList.isEmpty()) throw IllegalStateException("Nothing was extracted from the given file")
    // Watch out! Temporary solution to assign 3rd building to all the pairs by default, as it seems to be default for first courses.
    return extractorList.map { it.result.apply { buildingID = 3 } }
}