package com.raritasolutions.mymining

import com.raritasolutions.mymining.converter.RebornExcelConverter
import com.raritasolutions.mymining.dump.RawDumpTool
import com.raritasolutions.mymining.model.filesystem.toCachedFile
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class DumpToolsTest {

    @Test
    fun testRawDumpTool() {

        val processedFiles = Files.walk(Path.of("cached/processed/"))
                .map(Path::toFile)
                .filter(File::isFile)
                .map { it.toCachedFile() }
                .toList()

        val converter = RebornExcelConverter()
        val data = processedFiles
                .map {
                    val building = it.getDefaultBuilding()
                    val inputStream = it.fileContents.inputStream()
                    //Pair(building, converter.convert(inputStream, building))
                    converter.convert(inputStream, building)
                }
                .flatten()


        val rdt = RawDumpTool()
        val output = Files.newOutputStream(Path.of("cached/out.xlsx"))
        rdt.dumpData(data, 3, output)

    }

}