package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.extractor.cell.CellExtractorFactory
import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.utils.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException


class RawConverter(private val rawList: List<RawPairRecord>){

    private val _extractorList = arrayListOf<ContentSafeExtractor>()
    val extractorList: ArrayList<ContentSafeExtractor>
        get(){
            _extractorList.clear()
            rawList.filter { it.contents.isNotBlank() }.forEach { it.split() }
            return _extractorList
        }

    private fun RawPairRecord.split(){
        val _contents = contents.replace("_","")
        when {
            multiplePairRegexVanilla.matches(_contents)
                    -> addContents(this.toPairRecord(),ContentsSplitter(_contents, ripVanillaRegex).result)
            multiplePairRegexOneHalf.matches(_contents)
                    -> addContents(this.toPairRecord(),ContentsSplitter(_contents, ripOneHalfRegex).result)
            else -> addContents(this.toPairRecord(),listOf(_contents))
        }
    }
    // this method shouldn't be that condensed
    private fun RawPairRecord.toPairRecord(): PairRecord
    {
        val daysOfTheWeek = mapOf("понедельник" to 1, "вторник" to 2, "среда" to 3, "четверг" to 4,"пятница" to 5)
        return PairRecord(day = daysOfTheWeek[this.day] ?: throw IllegalStateException("Day of the week ${this.day} is illegal"),
                          timeSpan = this.timeSpan,
                          group = this.group)
    }

    private fun addContents(basePair: PairRecord, contentList : List<String>)
        = contentList.forEach {
        if (multiplePairRegexOneLine.containsMatchIn(it))
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


