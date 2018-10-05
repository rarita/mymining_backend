package com.raritasolutions.mymining.fetcher

import com.raritasolutions.mymining.extractor.RawConverter
import com.raritasolutions.mymining.extractor.getRawListFromCSV
import com.raritasolutions.mymining.model.PairRecord
import java.io.File
import java.io.StringReader
import java.lang.IllegalStateException

fun String.removeFirstLine()
        = this.substringAfter("\n")

fun txtToPairRecordList(filename: String): List<PairRecord>
{
    val source = File(filename)
            .readText()
            .replace("\r"," ")
            .removeFirstLine()
    val sourceReader = StringReader(source)
    val rawList = getRawListFromCSV(sourceReader)
    val extractorList = RawConverter(rawList).extractorList
    extractorList.forEach { it.make() }
    if (extractorList.isEmpty()) throw IllegalStateException("Nothing was extracted from the given file")
    return extractorList.map { it.result }
}