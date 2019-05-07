package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.repo.PairRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/api")
class RESTController @Autowired constructor(private val pairRepo: PairRepository) {

    @GetMapping("/schedule")
    @ResponseBody
    fun fetchGeneric(@RequestParam(value="room",required = false) room: String?,
                     @RequestParam(value="teacher",required = false) teacher: String?,
                     @RequestParam(value="group",required = false) group: String?): Any
            =   when {
        room != null -> pairRepo.findByRoomContaining(room)
        teacher != null -> pairRepo.findByTeacherContaining(teacher)
        group != null -> if (group.endsWith('а'))
                            pairRepo.findByGroup(group.substringBeforeLast('а')) + pairRepo.findByGroup(group)
                         else
                             pairRepo.findByGroup(group)
        else -> ResponseEntity.badRequest().body(null)
    }
}