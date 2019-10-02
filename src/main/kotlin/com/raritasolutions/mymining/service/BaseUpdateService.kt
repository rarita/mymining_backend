package com.raritasolutions.mymining.service

import com.raritasolutions.mymining.analyser.BaseWebAnalyser
import com.raritasolutions.mymining.converter.BaseConverter
import com.raritasolutions.mymining.extractor.RawConverter
import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.filesystem.CachedFile
import com.raritasolutions.mymining.model.filesystem.toCachedFile
import com.raritasolutions.mymining.repo.PairRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.net.URL

abstract class BaseUpdateService (private val pairRepo: PairRepository,
                         private val cacheService: CacheService,
                         private val analyser: BaseWebAnalyser,
                         private val converter: BaseConverter,
                         private val okHttpClient: OkHttpClient,
                         val report: ExtractionReport) {

    init {
        converter.report = report
    }

    private fun CachedFile.getDefaultBuilding()
        = if (!fileName.contains("км") && fileName[0] in '1'..'2') 3
          else 1

    private fun URL.getResponse(): Response {
        val request = Request.Builder()
                .url(this)
                .get()
                .build()

        return okHttpClient.newCall(request).execute()
    }

    open fun process(files: List<CachedFile>)
        = files

    // todo: make the following code as failsafe as possible
    // todo: this piece of code is a total abomination and should be refactored ASAP
    fun update() {

        val isColdBoot = (cacheService.isEmpty() || pairRepo.count() == 0L)
        val linksToLoad = analyser.analyse()
        // Find out what files should be updated
        val remoteFiles = linksToLoad
                .mapValues { it.value.getResponse() }
                .map { it.value.toCachedFile(it.key) }

        // If some aliases were updated, drop entire cache and pairs DB and reload it
        // This should happen only once in a while
        // Also don't evaluate it if it is cold boot (no need)
        val aliasesWereUpdated = isColdBoot || !remoteFiles.all(cacheService::hasAlias)
        val filesToUpdate = if (aliasesWereUpdated) {
            cacheService.clearAll()
            pairRepo.deleteAll()
            remoteFiles
        }
        else
            remoteFiles.filterNot(cacheService::hasFile)

        // Bail out if there is nothing to update
        if (filesToUpdate.isEmpty())
            return

        // Process the files and store it in the persistent cache
        val processedFiles = process(filesToUpdate)
        processedFiles
                .filterNot { File("cached/new_xls/${it.fileName}").exists() }
                .forEach {
                    cacheService.updateFileWithAlias(it)
                    /*
                    println("[I] Saving ${it.fileName}...")
                    it.saveTo(Path.of("cached", "new_xls", it.fileName))
                     */
                }


        val extractors = processedFiles
                .map {
                    val building = it.getDefaultBuilding()
                    val inputStream = it.fileContents.inputStream()
                    Pair(building, converter.convert(inputStream, building))
                }
                .map { RawConverter(it.second, report, it.first).extractorList }
                .flatten()

        //val extractors = RawConverter(listOf(), report, 1).extractorList // Заглушка
        // Ugly workaround faulty cases
        val processedExtractors = arrayListOf<ContentSafeExtractor>()
        for (extractor in extractors){
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