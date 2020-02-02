package com.raritasolutions.mymining.service

import com.raritasolutions.mymining.analyser.BaseWebAnalyser
import com.raritasolutions.mymining.converter.BaseConverter
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.filesystem.CachedFile
import com.raritasolutions.mymining.pdf_processor.BasePDFProcessor
import com.raritasolutions.mymining.repo.PairRepository
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

private val logger = LoggerFactory.getLogger(RebornUpdateService::class.java)

@Service
class RebornUpdateService @Autowired constructor(pairRepo: PairRepository,
                                                 cacheService: CacheService,
                                                 @Qualifier("web") analyser: BaseWebAnalyser,
                                                 @Qualifier("reborn") converter: BaseConverter,
                                                 val pdfProcessor: BasePDFProcessor,
                                                 okHttpClient: OkHttpClient,
                                                 report: ExtractionReport) : BaseUpdateService(pairRepo, cacheService, analyser, converter, okHttpClient, report) {

    override fun process(files: List<CachedFile>): List<CachedFile> {

        // TODO Please rewrite me in functional style this is hella ugly
        val processedFiles = mutableListOf<CachedFile>()
        for (file in files) {
            try {
                processedFiles += pdfProcessor.processFile(file)
            }
            catch (e: Exception) {
                logger.error("There was an error during file processing: ${e.message}")
            }
        }
        //val processedFiles = files.map(pdfProcessor::processFile)

        return processedFiles
    }
            /* = File("cached/new_xls/").walk()
                .filter(File::isFile)
                .map { it.toCachedFile(it.name) }
                .toList() */

}

