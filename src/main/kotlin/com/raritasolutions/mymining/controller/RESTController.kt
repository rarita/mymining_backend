package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.repo.PairRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin
@RequestMapping("/api")
class RESTController @Autowired constructor(private val pairRepo: PairRepository) {

    @GetMapping("/schedule")
    @ResponseBody
    fun fetchGeneric(@RequestParam(value="room",required = false) room: String?,
                     @RequestParam(value="teacher",required = false) teacher: String?,
                     @RequestParam(value="group",required = false) group: String?,
                     @RequestParam(value="day",required = false) day: Int = 0,
                     @RequestParam(value="week", required = false) week: Int = 0): Any {
        val pairList = when {
            room != null -> pairRepo.findByRoomContaining(room)
            teacher != null -> pairRepo.findByTeacherContaining(teacher)
            group != null -> if (group.endsWith('а'))
                pairRepo.findByGroup(group.substringBeforeLast('а')) + pairRepo.findByGroup(group)
            else
                pairRepo.findByGroup(group)
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
                            @RequestParam(value = "value", required = false) value: String = "") : Any
        =   when (type) {
        "group" -> pairRepo.findDistinctFirst3ByGroupStartingWithOrderByGroup(value)
        "teacher" -> pairRepo.findDistinctFirst3ByTeacherContainingAndTeacherNotContainingOrderByTeacher(value)
        else -> ResponseEntity.badRequest().body(null)
    }
}