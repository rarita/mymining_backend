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
        assert("id" in result.keys)
        assert("DEFAULT" in result.values)
    }

    @Test
    fun testListOfProperties()
    {
        val properties = PairRecord().listOfProperties()
        assert("buildingID 0" in properties)
        assert("id 0" in properties)
        assert("needsRevision false" in properties)
    }

    @Test
    fun testPairRecordToString()
    {
        val pair = PairRecord().apply {
            week = 1
            one_half = true
        }
        assert(pair.toString("id","room","subject", "teacher","week","one_half") ==
                "0, 0, Default, DEFAULT, I, 1/2")
    }
}