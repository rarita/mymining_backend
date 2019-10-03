package com.raritasolutions.mymining.service

import com.raritasolutions.mymining.analyser.BaseWebAnalyser
import com.raritasolutions.mymining.converter.BaseConverter
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.filesystem.CachedFile
import com.raritasolutions.mymining.pdf_processor.BasePDFProcessor
import com.raritasolutions.mymining.repo.PairRepository
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class RebornUpdateService @Autowired constructor(pairRepo: PairRepository,
                                                 cacheService: CacheService,
                                                 @Qualifier("firstcourseweb") analyser: BaseWebAnalyser,
                                                 @Qualifier("reborn") converter: BaseConverter,
                                                 val pdfProcessor: BasePDFProcessor,
                                                 okHttpClient: OkHttpClient,
                                                 report: ExtractionReport) : BaseUpdateService(pairRepo, cacheService, analyser, converter, okHttpClient, report) {

    override fun process(files: List<CachedFile>)
        = files.map(pdfProcessor::processFile)
             /* = File("cached/new_xls/").walk()
                .filter(File::isFile)
                .map { it.toCachedFile(it.name) }
                .toList() */

}

