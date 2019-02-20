package com.raritasolutions.mymining.composer

import com.raritasolutions.mymining.model.GroupFoldingSet
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.normalizeGroups
import com.raritasolutions.mymining.model.toString
import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.utils.DAYS_MAP
import com.raritasolutions.mymining.utils.EMPTY
import com.raritasolutions.mymining.utils.TIMES_LIST
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.servlet.ModelAndView

@Service
class DayTimeScheduleComposer @Autowired constructor(val pairRepo: PairRepository) {

    fun compose(stringRepresentation: PairRecord.() -> String, cond: (PairRecord) -> Boolean): ModelAndView
    {
        val viewModel = ModelAndView("generic_list")
        val model = viewModel.model
        // Request all entries from DB that match the condition
        // todo try to do SQL Request instead
        val pairsInRoom = pairRepo
                .findAll()
                .filter(cond)
                .filterNotNull()
        // Filling rows and cols and leaving top left cell of the table empty
        model["columns"] = listOf(String.EMPTY) + DAYS_MAP.values
        model["rows"] = TIMES_LIST
        model["data"] = mutableListOf<List<String>>()
        for (time in TIMES_LIST){
            val pairsAtGivenTime = pairsInRoom.filter { it.timeSpan == time }
            val rowRecords = mutableListOf(time)
            for (day in DAYS_MAP.keys){
                val pairsAtGivenDay = pairsAtGivenTime.filter { it.day == day }
                rowRecords +=
                        if (pairsAtGivenDay.isNotEmpty())
                            GroupFoldingSet(pairsAtGivenDay.toSet())
                                    .sortedBy { it.week }
                                    .map { it.apply { normalizeGroups() } }
                                    .joinToString(separator = "\n", transform = stringRepresentation)
                        else
                            String.EMPTY
            }
            (model["data"] as MutableList<List<String>>).add(rowRecords)
        }
        return viewModel
    }
}