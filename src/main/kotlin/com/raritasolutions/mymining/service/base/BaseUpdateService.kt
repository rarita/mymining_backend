package com.raritasolutions.mymining.service.base

import com.raritasolutions.mymining.analyser.BaseWebAnalyser
import com.raritasolutions.mymining.converter.BaseConverter
import com.raritasolutions.mymining.extractor.RawConverter
import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.filesystem.CachedFile
import com.raritasolutions.mymining.model.filesystem.toCachedFile
import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.service.CacheService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import java.net.URL

private val logger = LoggerFactory.getLogger(BaseUpdateService::class.java)

abstract class BaseUpdateService (private val pairRepo: PairRepository,
                                  private val cacheService: CacheService,
                                  private val analyser: BaseWebAnalyser,
                                  private val converter: BaseConverter,
                                  private val okHttpClient: OkHttpClient,
                                  val report: ExtractionReport) : UpdateSource {

    init {
        converter.report = report
    }

    private fun URL.getResponse(): Response {
        val request = Request.Builder()
                .url(this)
                .get()
                .build()

        logger.info("GET-Requesting $this...")
        return okHttpClient.newCall(request).execute()
    }

    open fun process(files: List<CachedFile>)
        = files

    // todo: make the following code as failsafe as possible
    // todo: this piece of code is a total abomination and should be refactored ASAP
    override fun update() {

        logger.info("Started data update process...")

        val isColdBoot = (cacheService.isEmpty() || pairRepo.count() == 0L)
        val linksToLoad = analyser.analyse()
        logger.info("Got ${linksToLoad.size} links from the links analyzer")

        // Find out what files should be updated
        val remoteFiles = linksToLoad
                .mapValues { it.value.getResponse() }
                .map { it.value.toCachedFile(it.key) }

        if (remoteFiles.size <= 10)
            throw IllegalStateException("Received insufficient amount of remote schedule files: ${remoteFiles.size}")

        // Save files to local FS
        // remoteFiles.forEach { it.saveTo(Path.of("cached/pdf", it.fileName)) }
        logger.info("Successfully retrieved ${remoteFiles.size} files from spmi.ru")

        // If some aliases were updated, drop entire cache and pairs DB and reload it
        // This should happen only once in a while
        // Also don't evaluate it if it is cold boot (no need)
        val aliasesWereUpdated = isColdBoot || !remoteFiles.all(cacheService::hasAlias)
        logger.info("Aliases were updated: $aliasesWereUpdated")

        val filesToUpdate = if (aliasesWereUpdated) {
            cacheService.clearAll()
            remoteFiles
        }
        else
            remoteFiles.filterNot(cacheService::hasFile)

        logger.info("Files to update size: ${filesToUpdate.size}, contents: $filesToUpdate")
        // Bail out if there is nothing to update
        if (filesToUpdate.isEmpty())
            return

        // Temporary action. Need to figure out what to leave and what to keep in the future
        // Probably should keep reference to parent file in the pair records
        logger.info("Deleting all pair data from the DB...")
        pairRepo.deleteAll()

        // Process the files and store it in the persistent cache
        logger.info("Starting to process raw PDF files...")
        val processedFiles = process(filesToUpdate)
        logger.info("Updating DB cache with ${processedFiles.size} processed files")
        processedFiles
                .forEach {
                    cacheService.updateFileWithAlias(it)
                }


        val extractors = processedFiles
                .map {
                    val building = it.getDefaultBuilding()
                    val inputStream = it.fileContents.inputStream()
                    Pair(building, converter.convert(inputStream, building))
                }
                .map { RawConverter(it.second, report, it.first).extractorList }
                .flatten()

        // Ugly workaround faulty cases
        val processedExtractors = arrayListOf<ContentSafeExtractor>()
        for (extractor in extractors) {
            try {
                extractor.make()
                processedExtractors += extractor
            }
            catch (e: Exception){
                report.addReport(e, extractor)
            }
        }

        if (processedExtractors.isEmpty())
            report.addMessage("Nothing was extracted from ${processedFiles.joinToString()}")

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