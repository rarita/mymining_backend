package com.raritasolutions.mymining

import com.raritasolutions.mymining.converter.LegacyCSVConverter
import com.raritasolutions.mymining.converter.TabulaConverter
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.RawPairRecord
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.springframework.core.io.ClassPathResource

class ConvertersTest{

    @Test
    fun testRealCSVOutputNoMilitaryClass()
    {
        val parsedTXT = ClassPathResource("/textdata/parsed.txt").file
        val converter = LegacyCSVConverter()
        val rawList = converter.convert(parsedTXT)
        dummyListAssertions(rawList)
    }

    @Test
    fun testTabulaConverter()
    {
        val converter = TabulaConverter()
        val rawList = converter.convert(FileUtils.getFile("dummy/tablev2.pdf"))
        dummyListAssertions(rawList)
    }

    private fun dummyListAssertions(rawList : List<RawPairRecord>)
    {
        // Test Pair Instances in table
        val pairsToTest = listOf(RawPairRecord("СРЕДА","12.35-14.05","ГНГ-18-1","Общая геология Проф. Таловина И.В. No832"),
                RawPairRecord("ПЯТНИЦА", "14.15-15.45","НГД-18-10","История Асс. Подольский С.И. пр. No612"),
                RawPairRecord("ЧЕТВЕРГ", "8:50-10:20","НГД-18-7","Начертательная геометрия и инженерная компьютерная графика Асс. Чупин С.А. пр. No710 Асс. Кононов П.В. пр. No726"))
        assert(rawList.intersect(pairsToTest).isNotEmpty())
        // Test Pairs quantity
        assert(rawList.size == 720) // 24x30 group table
    }
}
