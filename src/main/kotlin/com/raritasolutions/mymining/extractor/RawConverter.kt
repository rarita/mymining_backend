package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.extractor.cell.CellExtractorFactory
import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.utils.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

// todo make component
class RawConverter(private val rawList: List<RawPairRecord>,
                   private val report: ExtractionReport) {

    private val _extractorList = arrayListOf<ContentSafeExtractor>()
    val extractorList: ArrayList<ContentSafeExtractor>
        get(){
            _extractorList.clear()
            rawList
                    .filter { it.contents.isNotBlank() }
                    .forEach { it.split() }
            return _extractorList
        }

    private fun RawPairRecord.split() {
        val _contents = contents
                .replace("_","")
                .replace("\\s+".toRegex(), " ")
        try {
            addContents(this.toPairRecord(),PairSplitter(_contents).contents)
        }
        catch (e: Exception){ report.addReport(e, this) }
    }

    // this method shouldn't be that condensed
    private fun RawPairRecord.toPairRecord(): PairRecord
    {
        val daysOfTheWeek = mapOf("ПОНЕДЕЛЬНИК" to 1, "ВТОРНИК" to 2, "СРЕДА" to 3, "ЧЕТВЕРГ" to 4, "ПЯТНИЦА" to 5)
        return PairRecord(day = daysOfTheWeek[this.day] ?: throw IllegalStateException("Day of the week ${this.day} is illegal"),
                          timeSpan = this.timeSpan,
                          group = this.group)
    }

    private fun addContents(basePair: PairRecord, contentList : List<String>)
        = contentList.forEach {
        // If it contains simple rooms as well as multi-rooms it should be handled by the Extractor implementation
        if (multiplePairRegexOneLine.containsMatchIn(it)
                && !it.replace(multiplePairRegexOneLine, "").contains("No"))
            addEnhancedContents(basePair,it,ContentsSplitter(it, ripOneLineRegex).result)
        else
            _extractorList.add(CellExtractorFactory(it,basePair).produce())
    }
    private fun addEnhancedContents(basePair: PairRecord, originalContents: String, rooms: List<String>)
    {
        if (rooms.size != 2)
            throw IllegalArgumentException("Rooms size must be exact 2 (now is ${rooms.size}")

        for (week in rooms.indices){
            basePair.week = week+1
            basePair.room = rooms[week].stripPrefix("No")
            _extractorList.add(CellExtractorFactory(originalContents.replace(multiplePairRegexOneLine,""),
                                                    basePair).produce())
        }
    }

}


