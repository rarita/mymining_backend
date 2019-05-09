package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.extractor.cell.CellExtractorFactory
import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.extractor.splitter.RebornSplitter
import com.raritasolutions.mymining.model.*
import com.raritasolutions.mymining.utils.multiplePairRegexOneLine
import com.raritasolutions.mymining.utils.ripOneLineRegex
import com.raritasolutions.mymining.utils.roomNumberTokenRegex
import com.raritasolutions.mymining.utils.substringAfterRegex
import kotlin.collections.set

// todo make component
class RawConverter(private val rawList: List<RawPairRecord>,
                   private val report: ExtractionReport,
                   private val defaultBuilding: Int = 3) {

    private val _extractorList = arrayListOf<ContentSafeExtractor>()
    val extractorList: ArrayList<ContentSafeExtractor>
        get(){
            _extractorList.clear()
            rawList
                    .filter { it.contents.isNotBlank() }
                    .forEach { it.split() }
            return _extractorList
        }

    /**
     * Small extension to simplify adding up values of Mutable Map
     * @param key Key of the element that needs to be added up
     * @param value Value that will be added to this[key] element
      */
    private fun MutableMap<Int, Int>.addUp(key: Int, value: Int) {
        val prevValue = this[key]
                ?: throw IllegalStateException("Element with the key $key is not present at $this map")
        this[key] = prevValue + value
    }

    private fun findBuildingId(initialContents: String,
                               subContents: String,
                               formatting: List<BuildingData>) : Int {
        val startPosition = initialContents.indexOf(subContents)
        val endPosition = startPosition + subContents.length
        val subFormatting = formatting.slice(startPosition, endPosition)
        // In given subFormatting count characters for each present building
        val buildingCharacters = mutableMapOf(1 to 0, 2 to 0, 3 to 0)
        for (runIndex in 1 until subFormatting.size) {
            val currentRun = subFormatting[runIndex]
            val prevRun = subFormatting[runIndex - 1]
            buildingCharacters.addUp(
                    key = prevRun.buildingId,
                    value = currentRun.startIndex - prevRun.startIndex)
        }
        // Finally, calculate length of the last formatting run
        buildingCharacters.addUp(
                key = subFormatting.last().buildingId,
                value = endPosition - subFormatting.last().startIndex)
        // When we know how much characters belong to each building
        // Find out the main building of the string and return it
        return buildingCharacters.maxBy { it.value }?.key!!
    }

    private fun RawPairRecord.split() {
        // The following line has no effect on contents received from RebornConverter
        // That's why building data should not be recalculated afterwards
        val _contents = contents
                .replace("_","")
                .replace("\\s+".toRegex(), " ")
        try {
            val basePairRecord = this.toPairRecord()
            // If RawPairRecord has single or none records of buildingData assign building immediately
            // In other cases pass formatting to the addContents to determine buildings for separate records
            when {
                this.formatting == null -> {
                    basePairRecord.buildingID = defaultBuilding
                    addContents(basePairRecord, RebornSplitter(_contents).separatedContents, _contents)
                }
                this.formatting.size == 1 -> {
                    basePairRecord.buildingID = this.formatting.first().buildingId
                    addContents(basePairRecord, RebornSplitter(_contents).separatedContents, _contents)
                }
                else -> addContents(basePairRecord, RebornSplitter(_contents).separatedContents, _contents, formatting)
            }
        }
        catch (e: Exception){ report.addReport(e, this) }
    }

    // this method shouldn't be that condensed
    private fun RawPairRecord.toPairRecord(): PairRecord
        = PairRecord(day = this.day,
                     timeSpan = this.timeSpan,
                     group = this.group)


    private fun addContents(basePair: PairRecord,
                            contentList : List<String>,
                            initialContents: String,
                            formatting: List<BuildingData>? = null)
        = contentList.forEach {
        // If the formatting was passed process it and apply the correct building to the basePair
        if (formatting != null)
            basePair.buildingID = findBuildingId(initialContents, it, formatting)
        // If it contains simple rooms as well as multi-rooms it should be handled by the Extractor implementation
        if (multiplePairRegexOneLine.containsMatchIn(it)
                && !it.replace(multiplePairRegexOneLine, "").contains("No"))
            addEnhancedContents(basePair,it,ContentsSplitter(it, ripOneLineRegex).result)
        else
            _extractorList.add(CellExtractorFactory(it, basePair).produce())
    }

    private fun addEnhancedContents(basePair: PairRecord,
                                    originalContents: String,
                                    rooms: List<String>)
    {
        if (rooms.size != 2)
            throw IllegalArgumentException("Rooms size must be exact 2 (now is ${rooms.size}")

        for (week in rooms.indices){
            val prefix = if (rooms[week].contains(roomNumberTokenRegex)) roomNumberTokenRegex else "-".toRegex()
            basePair.week = week+1
            basePair.room = rooms[week]
                    .substringAfterRegex(prefix)
                    .replace(roomNumberTokenRegex, "")
                    .trim()
            _extractorList.add(CellExtractorFactory(originalContents.replace(multiplePairRegexOneLine,""),
                                                    basePair).produce())
        }
    }

}


