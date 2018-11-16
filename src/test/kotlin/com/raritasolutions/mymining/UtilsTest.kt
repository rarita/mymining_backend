package com.raritasolutions.mymining

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.toString
import com.raritasolutions.mymining.utils.listOfProperties
import com.raritasolutions.mymining.utils.toPropertyMap
import org.junit.Before
import org.junit.Test

class UtilsTest {

    @Test
    fun testObjectToMap(){
        val pair = PairRecord()
        val result = pair.toPropertyMap()
        result.forEach { println("${it.key} ${it.value}")}
    }

    @Test
    fun testListOfProperties()
    {
        println(PairRecord().listOfProperties())
    }

    @Test
    fun testPairRecordToString()
    {
        val pair = PairRecord().apply {
            week = 1
            one_half = true
        }
        println(pair.toString("id","room","subject", "teacher","week","one_half"))
    }
}