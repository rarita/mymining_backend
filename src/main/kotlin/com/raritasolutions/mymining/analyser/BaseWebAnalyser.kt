package com.raritasolutions.mymining.analyser

import org.springframework.stereotype.Service
import java.net.URL

@Service
interface BaseWebAnalyser {
    fun analyse(): Map<String, URL>
}