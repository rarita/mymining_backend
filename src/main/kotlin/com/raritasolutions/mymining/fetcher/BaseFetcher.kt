package com.raritasolutions.mymining.fetcher

import org.springframework.stereotype.Service
import java.io.File

// Gets PDFs from various data sources
@Service
interface BaseFetcher {
    fun fetchSchedule(): Map<String,File>
}