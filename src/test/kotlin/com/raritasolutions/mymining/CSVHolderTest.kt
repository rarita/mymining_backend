package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.getRawListFromCSV
import com.raritasolutions.mymining.model.RawPairRecord
import org.junit.Test
import java.io.File
import java.io.StringReader

fun String.removeFirstLine() = this.substringAfter("\n")

class CSVHolderTest{
    @Test
    fun testRealCSVOutputNoMilitaryClass()
    {
        val source = File("C:\\Users\\rarita\\Documents\\decompose_tables\\out_v2.txt")
                .readText()
                .replace("\r"," ")
                .removeFirstLine()
        val sourceReader = StringReader(source)
        val rawList = getRawListFromCSV(sourceReader)
        rawList.forEach { println(it) }
        // Test Pair Instances in table
        val pairsToTest = listOf(RawPairRecord("среда","12.35-14.05","ГНГ-18-1","Общая геология Проф. Таловина И.В. No832"),
                                 RawPairRecord("пятница", "14.15-15.45","НГД-18-10","История Асс. Подольский С.И. пр. No612"),
                                 RawPairRecord("четверг", "8:50-10:20","НГД-18-7","Начертательная геометрия и инженерная компьютерная графика Асс. Чупин С.А. пр. No710 Асс. Кононов П.В. пр. No726"))
        assert(rawList.intersect(pairsToTest).isNotEmpty())
        // Test Pairs quantity
        assert(rawList.size == 720) // 24x30 group table
    }

}
