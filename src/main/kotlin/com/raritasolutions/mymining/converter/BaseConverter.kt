package com.raritasolutions.mymining.converter

import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.RawPairRecord
import org.springframework.stereotype.Component
import java.io.File

@Component
interface BaseConverter {
    var report: ExtractionReport?
    fun convert(localFile: File): List<RawPairRecord>
    fun convertAll(localFiles : List<File>)
        = localFiles.map { convert(it) } .flatMap { it.toList() }
}