package com.raritasolutions.mymining.converter

import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.RawPairRecord
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
interface BaseConverter {
    var report: ExtractionReport?
    /**
     * Sends Converter a command to convert given input file.
     * @param data target [InputStream], either file or in-memory byte array
     * @param defaultBuilding represents default building, where
     * classes with white background and black text are held
     * This parameter is only needed for converters that can
     * work with the colors, like RebornConverter
     */
    fun convert(data : InputStream, defaultBuilding: Int): List<RawPairRecord>
}