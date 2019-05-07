package com.raritasolutions.mymining.converter

import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.RawPairRecord
import org.springframework.stereotype.Component
import java.io.File

@Component
interface BaseConverter {
    var report: ExtractionReport?
    /**
     * Sends Converter a command to convert given input file.
     * @param localFile target file, either pdf of xls
     * @param defaultBuilding represents default building, where
     * classes with white background and black text are held
     * This parameter is only needed for converters that can
     * work with the colors, like RebornConverter
     */
    fun convert(localFile : File, defaultBuilding: Int): List<RawPairRecord>
}