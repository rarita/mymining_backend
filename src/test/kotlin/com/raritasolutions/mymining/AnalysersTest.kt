package com.raritasolutions.mymining

import com.raritasolutions.mymining.analyser.FirstCourseScheduleAnalyser
import org.junit.Test

class AnalysersTest {
    @Test
    fun testFirstCourseAnalyser(){
        val fca = FirstCourseScheduleAnalyser()
        val links = fca.analyse()
        assert(links.size == 4)
        assert("Строительный факультет, Экономический факультет" in links.keys)
        assert( true.apply { links.values.random().openConnection() } )
    }
}