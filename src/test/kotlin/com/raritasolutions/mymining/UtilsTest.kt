package com.raritasolutions.mymining

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.toPropertyMap
import org.junit.Test

class UtilsTest {

    @Test
    fun testObjectToMap(){
        val pair = PairRecord()
        val result = pair.toPropertyMap()
        result.forEach { println("${it.key} ${it.value}")}
    }
}