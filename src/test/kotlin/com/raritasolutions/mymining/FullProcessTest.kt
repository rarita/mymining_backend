package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.RawConverter
import com.raritasolutions.mymining.extractor.getRawListFromCSV
import org.junit.Test
import java.io.File
import java.io.StringReader

class FullProcessTest {
    @Test
    fun testWholeProcessWithSimpleInput()
    {
        val source = File("C:\\Users\\rarita\\Documents\\decompose_tables\\out_v2.txt")
                .readText()
                .replace("\r"," ")
                .removeFirstLine()
        val sourceReader = StringReader(source)
        val rawList = getRawListFromCSV(sourceReader)
        val extractorList = RawConverter(rawList).extractorList
        extractorList.forEach { it.make() }
        val pairsList = extractorList.map { it.result }
        val testValues = pairsList.filter { it.group == "ГНГ-18-1" && it.day == 4 }
        assert(testValues.size == 6)
        //testValues.forEach { println(it) }
    }
}