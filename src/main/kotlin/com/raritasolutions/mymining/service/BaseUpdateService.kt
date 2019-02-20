package com.raritasolutions.mymining.service

import com.raritasolutions.mymining.analyser.BaseWebAnalyser
import com.raritasolutions.mymining.converter.BaseConverter
import com.raritasolutions.mymining.extractor.RawConverter
import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.repo.CacheRepository
import com.raritasolutions.mymining.repo.PairRepository
import java.lang.Exception

abstract class BaseUpdateService (private val pairRepo: PairRepository,
                         private val cacheRepo: CacheRepository,
                         private val analyser: BaseWebAnalyser,
                         private val converter: BaseConverter,
                         val report: ExtractionReport) {

    init {
        converter.report = report
    }

    fun update() {
        val isColdBoot = (cacheRepo.localFiles.isEmpty() || pairRepo.count() == 0L)
        val linksToLoad = analyser.analyse()
        // True if any of the files were overwritten
        val needsUpdate = linksToLoad
                        .map { cacheRepo.saveFile(it.value)}
                        .any { it }

        if (needsUpdate || isColdBoot) {
            // If it doesn't need to be compared and updated just load all the pairs to the DB
            val files = cacheRepo.localFiles
            val rawPairs = files
                    .map(converter::convert)
                    .flatten()
            val extractors = RawConverter(rawPairs, report).extractorList
            // Ugly workaround faulty cases
            val processedExtractors = arrayListOf<ContentSafeExtractor>()
            for (extractor in extractors){
                try {
                    extractor.make()
                    processedExtractors += extractor
                }
                catch (e: Exception){
                    report.addReport(e,extractor)
                }
            }

            if (processedExtractors.isEmpty()) throw Exception("Nothing was extracted from the $files")
            val results = arrayListOf<PairRecord>()
            for (pe in processedExtractors) {
                try {
                    results += pe.result
                }
                catch (e: Exception){
                    report.addReport(e, pe)
                }
            }
            pairRepo.saveAll(results)
        }
    }

    fun updateDB(updatedPairs: List<PairRecord>) {
        val diff = updatedPairs - pairRepo.findAll()
    }
}