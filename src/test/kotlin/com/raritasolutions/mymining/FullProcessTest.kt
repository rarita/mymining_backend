package com.raritasolutions.mymining

import com.raritasolutions.mymining.fetcher.txtToPairRecordList
import org.junit.Test

class FullProcessTest {
    @Test
    fun testWholeProcessWithSimpleInput()
    {
        val pairsList =
                txtToPairRecordList("C:\\Users\\rarita\\Documents\\decompose_tables\\out_v2.txt")
        val testValues = pairsList.filter { it.group == "ГНГ-18-1" && it.day == 4 }
        assert(testValues.size == 6)
        //testValues.forEach { println(it) }
    }
}