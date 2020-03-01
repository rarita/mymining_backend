package com.raritasolutions.mymining

import com.raritasolutions.mymining.converter.RebornWordConverter
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path

class WordExtractorTest {

    @Test
    fun testWordManipulation() {
        val rwc = RebornWordConverter()
        rwc.convert(Files.newInputStream(Path.of("test/2kstrek.docx")), 1)
    }

}