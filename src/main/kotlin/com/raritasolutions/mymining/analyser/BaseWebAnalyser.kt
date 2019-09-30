package com.raritasolutions.mymining.analyser

import org.springframework.stereotype.Component
import java.net.URL

@Component
interface BaseWebAnalyser {

    fun analyse(): Map<String, URL>

}