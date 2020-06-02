package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.model.GroupFoldingSet
import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.repo.new.IntermediateRepository
import com.raritasolutions.mymining.repo.new.NormalizedRepository
import com.raritasolutions.mymining.service.ScheduleTimeService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin
@RequestMapping("/api")
class RESTController (private val pairRepo: PairRepository,
                      private val normalizedRepository: NormalizedRepository,
                      private val intermediateRepository: IntermediateRepository,
                      private val scheduleTimeService: ScheduleTimeService) {

    @GetMapping("/schedule")
    @ResponseBody
    fun fetchGeneric(@RequestParam(value="room",required = false) room: String?,
                     @RequestParam(value="teacher",required = false) teacher: String?,
                     @RequestParam(value="group",required = false) group: String?,
                     @RequestParam(value="day",required = false) day: Int = 0,
                     @RequestParam(value="week", required = false) week: Int = 0,
                     @RequestParam(value="datasource", required = false, defaultValue = "normalized")
                     dataSource: String ) : Any {

        val pairList =
                when (dataSource) {

                    "legacy" -> when {
                        room != null -> pairRepo.findByRoomContaining(room)
                        teacher != null -> {
                            val pairs = pairRepo.findByTeacherContaining(teacher)
                            GroupFoldingSet(pairs.toSet())
                        }
                        group != null -> if (group.endsWith('а'))
                            pairRepo.findByGroup(group.substringBeforeLast('а')) + pairRepo.findByGroup(group)
                        else
                            pairRepo.findByGroup(group)
                        else -> return ResponseEntity.badRequest().body(null)
                    }

                    "normalized" -> {
                        when {
                            group != null -> intermediateRepository.findAllPairRecordsByGroup(group)
                            teacher != null -> intermediateRepository.findAllPairRecordsByTeacher(teacher)
                            room != null -> intermediateRepository.findAllPairRecordsByRoom(room)
                            else -> return ResponseEntity.badRequest().body(null)
                        }
                    }

                        else -> return ResponseEntity.badRequest().body(null)
                }
        return pairList
                .filter { if (day != 0) it.day == day else true }
                .filter {
                    when (week) {
                        1 -> it.week != 2
                        2 -> it.week != 1
                        else -> true
                    }
                }
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    fun fetchAutocompletion(@RequestParam(value = "type", required = true) type: String,
                            @RequestParam(value = "datasource", required = false, defaultValue = "normalized")
                            dataSource: String,
                            @RequestParam(value = "value", required = false, defaultValue = "")
                            value: String) : Any

        =  when (dataSource) {

        "legacy" -> when (type) {
            "group" -> pairRepo.findDistinctFirst3ByGroupStartingWithOrderByGroup(value)
            "teacher" -> pairRepo.findDistinctFirst3ByTeacherContainingAndTeacherNotContainingOrderByTeacher(value)
            else -> ResponseEntity.badRequest().body(null)
        }

        "normalized" -> when (type) {
            "group" -> normalizedRepository.findGroupsLike(value) // Set<GroupName>
            "teacher" -> normalizedRepository.findTeachersLike(value)
            else -> ResponseEntity.badRequest().body(null)
        }

        else -> ResponseEntity.badRequest().body(null)
    }

    @GetMapping("/week")
    @ResponseBody
    fun fetchCurrentWeek()
        = scheduleTimeService.getCurrentWeek()

    @GetMapping("/day")
    @ResponseBody
    fun fetchNextWorkingDay()
        = scheduleTimeService.getCurrentDay()

}