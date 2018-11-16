package com.raritasolutions.mymining

import com.raritasolutions.mymining.model.toString
import com.raritasolutions.mymining.repo.PairRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.ModelAndView

class ScheduleComposers(@Autowired val pairRepo: PairRepository) {

    // Working with generic table to avoid repeating code
    fun composeRoomSchedule(room : String): ModelAndView{
        val model = mutableMapOf<String,Iterable<*>>()
        val pairsInRoom = pairRepo.findByRoom(room)
        val times = pairsInRoom.map { it.timeSpan }.toSortedSet()
        val days = pairsInRoom.map { it.day }.toSortedSet()
        model["data"] = mutableListOf<Iterable<*>>(days)
        // rows are times cols are days
        for (time in times){
            val pairsAtGivenTime = pairsInRoom.filter { it.timeSpan == time }
            val rowRecords = mutableListOf<String>()
            for (day in days){
                val pairsAtGivenDay = pairsAtGivenTime.filter { it.day == day }
                if (pairsAtGivenDay.isEmpty())
                    rowRecords += ""
                else
                    rowRecords += pairsAtGivenDay.joinToString(separator = "\n") { it.toString("one_half", "week", "subject", "teacher", "type") }
            }
        }
        return ModelAndView("generic_list",model)
    }
}