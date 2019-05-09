package com.raritasolutions.mymining

import com.raritasolutions.mymining.analyser.FirstCourseScheduleAnalyser
import org.junit.Test

class AnalysersTest {
    @Test
    fun testFirstCourseAnalyser(){
        val fca = FirstCourseScheduleAnalyser()
        val links = fca.analyse()
        assert(links.size == 4)
        assert("1 курс (Факультет переработки минерального сырья, Горный факультет)" in links.keys)
        assert( true.apply { links.values.random().openConnection() } )
    }
}