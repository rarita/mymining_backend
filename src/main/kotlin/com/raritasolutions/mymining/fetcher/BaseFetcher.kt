package com.raritasolutions.mymining.fetcher

import java.io.File

// Gets PDFs from various data sources
interface BaseFetcher {
    fun getFiles(): Map<String,File>
}