package com.raritasolutions.mymining.pdf_processor

import com.raritasolutions.mymining.model.filesystem.CachedFile

interface BasePDFProcessor {

    fun processFile(cachedFile: CachedFile): CachedFile

}