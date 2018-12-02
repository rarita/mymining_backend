package com.raritasolutions.mymining

import com.raritasolutions.mymining.converter.LegacyCSVConverter
import org.junit.Test
import org.springframework.core.io.ClassPathResource

class FullProcessTest {
    @Test
    fun testWholeProcessWithSimpleInput()
    {
        val pairsList = LegacyCSVConverter().convert(ClassPathResource("textdata/parsed.txt").file)
        val testValues = pairsList.filter { it.group == "ГНГ-18-1" && it.day == "ЧЕТВЕРГ" }
        assert(testValues.size == 6)
    }
}