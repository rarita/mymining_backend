package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.CSVParser
import org.junit.Test

class CSVParserTest
{
    @Test(expected = Exception::class)
    fun parseEmptyList()
    {
        val emptyList = listOf<String>()
        CSVParser(emptyList).parse()
    }

    @Test(expected = Exception::class)
    fun parseInconsistentList()
    {
        val incorrectList = listOf("123,456",
                                   "789,012,345")
        CSVParser(incorrectList).parse()
    }

    @Test
    fun parseCorrectList()
    {
        val correctList = listOf("1,2,3",
                                 "4,5,6",
                                 "7,8,9")
        val result = CSVParser(correctList).parse()
        assert(result.contains(listOf("1","2","3")))
    }
}