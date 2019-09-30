package com.raritasolutions.mymining

import com.raritasolutions.mymining.model.NO_ROOM
import com.raritasolutions.mymining.model.NO_TEACHER
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.toString
import com.raritasolutions.mymining.utils.listOfProperties
import com.raritasolutions.mymining.utils.toPropertyMap
import org.junit.Test

class UtilsTest {

    @Test
    fun testObjectToMap(){
        val pair = PairRecord()
        val result = pair.toPropertyMap()
        assert("id" in result.keys)
        assert("Default" in result.values)
    }

    @Test
    fun testListOfProperties()
    {
        val properties = PairRecord().listOfProperties()
        assert("buildingID" in properties)
        assert("id" in properties)
        assert("locked" in properties)
    }

    @Test
    fun testPairRecordToString()
    {
        val pair = PairRecord().apply {
            week = 1
            one_half = "1/2"
        }
        assert(pair.toString("id","room","subject", "teacher","week","one_half") ==
                "0, $NO_ROOM, Default, $NO_TEACHER, I, 1/2")
    }
}