package com.raritasolutions.mymining.composer

import com.raritasolutions.mymining.model.GroupFoldingSet
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.normalizeGroups
import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.utils.DAYS_NAMES_MAP
import com.raritasolutions.mymining.utils.EMPTY
import com.raritasolutions.mymining.utils.FIRST_TEN_ROMANS
import com.raritasolutions.mymining.utils.TIMES_LIST
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.servlet.ModelAndView

@Service
class DayTimeScheduleComposer @Autowired constructor(val pairRepo: PairRepository) {

    fun compose(stringRepresentation: PairRecord.() -> String,
                caption: String,
                cond: (PairRecord) -> Boolean) : ModelAndView {

        val viewModel = ModelAndView("generic_list_fancy")
        val model = viewModel.model
        val pairsMatching = pairRepo
                .findAll()
                .filter(cond)
                .filterNotNull()
        model["caption"] = caption
        model["rows"] = TIMES_LIST
        model["data"] = mutableListOf<List<String>>()
        for (time in TIMES_LIST.withIndex()){
            val pairsAtGivenTime = pairsMatching.filter { it.timeSpan == time.value }
            val rowRecords = mutableListOf(FIRST_TEN_ROMANS[time.index], time.value)
            for (day in DAYS_NAMES_MAP.keys) {
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