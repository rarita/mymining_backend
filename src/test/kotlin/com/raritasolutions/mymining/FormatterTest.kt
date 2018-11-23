package com.raritasolutions.mymining

import com.raritasolutions.mymining.model.GroupFoldingSet
import com.raritasolutions.mymining.model.PairRecord
import org.junit.Test

class FormatterTest {

    private val pairs = listOf(
            PairRecord(id = 0, subject = "Геология", group = "ААА-11"),
            PairRecord(id = 1, subject = "Аэрология", group = "ААА-11"),
            PairRecord(id = 2, subject = "Приборостроение", group = "ААА-11"),
            PairRecord(id = 3, subject = "Геология", group = "БББ-22"),
            PairRecord(id = 4, subject = "Приборостроение", group = "ВВВ-33")
    )

    @Test
    fun testGroupFoldingSet(){
        val fSet = GroupFoldingSet(*pairs.toTypedArray())
        assert(fSet.size == 3)
        val addedSet = fSet + PairRecord(subject="Геология", group="НГД-16-12")
        assert(addedSet.size == 3)
        assert(addedSet.find{it.group == "ААА-11, ВВВ-33"} != null)
        assert(addedSet.find{it.group == "ААА-11, БББ-22, НГД-16-12"} != null)
        // Assert immutability of ID-s
        assert(addedSet.map { it.id } == (0..2).toList())
    }
}