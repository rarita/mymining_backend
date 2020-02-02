package com.raritasolutions.mymining

import com.raritasolutions.mymining.analyser.WebScheduleAnalyser
import org.junit.Test

class AnalysersTest {

    @Test
    fun testWebScheduleAnalyzer() {

        val wsa = WebScheduleAnalyser()
        val links = wsa.analyse()

        assert(links.isNotEmpty())
        assert("1 курс (Факультет переработки минерального сырья, Горный факультет)" in links.keys)
        assert( true.apply { links.values.random().openConnection() } )

    }

}