package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.model.new.*
import com.raritasolutions.mymining.repo.new.BatchRepository
import com.raritasolutions.mymining.repo.new.RoomRepository
import com.raritasolutions.mymining.repo.new.TeacherRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest


@Controller
class IndexController(val batchRepository: BatchRepository,
                      val roomRepository: RoomRepository,
                      val teacherRepository: TeacherRepository) {

    @GetMapping("/") fun index(requestParams: HttpServletRequest)
        = ModelAndView("index_ajax")

    @GetMapping("/status") fun status(): ModelAndView {
        val model = HashMap<String,String>()
        val millis = ManagementFactory.getRuntimeMXBean()?.uptime
                ?: throw IllegalStateException("Can't get RuntimeMXBean")
        with (TimeUnit.MILLISECONDS){
            model["days"] =     toDays(millis).toString()
            model["hours"] =   (toHours(millis)   % 24).toString().padStart(2,'0')
            model["minutes"] = (toMinutes(millis) % 60).toString().padStart(2,'0')
            model["seconds"] = (toSeconds(millis) % 60).toString().padStart(2,'0')
        }
        return ModelAndView("status", model)
    }

    @GetMapping("/test")
    @ResponseBody
    fun testFeatures(): Batch {
        // Сценарий реюза 1
        val teacher = Teacher(-1,
                "Доц. Иванов И.И.",
                null,
                null,
                null,
                TeacherRank.DOCENT,
                null,
                null)
        val persistedTeacher = teacherRepository.save(teacher)

        val room = Room(-1, "test", Building(-1, "Suka"))
        val persistedRoom = roomRepository.save(room)

        val batch = Batch(-1,
                0,
                "",
                false,
                 BatchType.CLASS,
                 mutableSetOf(persistedTeacher), mutableSetOf(persistedRoom))

        return batchRepository.save(batch)

    }

}
