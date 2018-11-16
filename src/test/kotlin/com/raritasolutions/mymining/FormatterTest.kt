package com.raritasolutions.mymining

import com.raritasolutions.mymining.model.FoldingSet
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.toString
import org.junit.Test

class FormatterTest {

    private val pairs = listOf(
            PairRecord(subject = "Геология", group = "ААА-11"),
            PairRecord(subject = "Аэрология", group = "ААА-11"),
            PairRecord(subject = "Приборостроение", group = "ААА-11"),
            PairRecord(subject = "Геология", group = "БББ-22"),
            PairRecord(subject = "Приборостроение", group = "ВВВ-33")
    )
    /*
    @Test
    fun testFolding(){
        val result = pairs.formatGroupSchedule()
        assert(result.split("\n").size == 12)
        assert(result.contains("ААА-11, БББ-22"))
        assert(result.contains("ААА-11, ВВВ-33"))
    }
    */
    @Test
    fun testFoldingSet(){
        val conds = arrayOf("subject","one_half","week","teacher")
        val fSet = FoldingSet(pairs[0],pairs[1],pairs[2],pairs[3],pairs[4],foldingProp = PairRecord::group){
            other ->
                this.toString(*conds) == other.toString(*conds)
        }
        fSet.forEach { println("${it.subject} - ${it.group}") }
    }
}