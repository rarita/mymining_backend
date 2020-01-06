package com.raritasolutions.mymining

import com.fasterxml.jackson.databind.ObjectMapper
import com.raritasolutions.mymining.config.OkHTTPConfig
import com.raritasolutions.mymining.model.filesystem.toCachedFile
import com.raritasolutions.mymining.pdf_processor.SimplyPDFProcessor
import org.junit.Test
import java.io.File

class PDFProcessorTest {

    // Fields that should be autowired by Spring
    private val okHTTPConfig = OkHTTPConfig()
    private val cookieJar = okHTTPConfig.cookieJar()
    private val interceptor = okHTTPConfig.interceptor()
    private val okHTTPClient = okHTTPConfig.okHttpClient(cookieJar, interceptor)


    @Test
    fun testSimplyPDFProcessor() {
        val spp = SimplyPDFProcessor(okHTTPClient, ObjectMapper(), "in-testing")
        val testFile1 = spp.processFile(File("test/test1.pdf").toCachedFile())
        val testFile2 = spp.processFile(File("test/test2.pdf").toCachedFile())
        assert(!testFile1.fileContents.contentEquals(testFile2.fileContents))
    }


}