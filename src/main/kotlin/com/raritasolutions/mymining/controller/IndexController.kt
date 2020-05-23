package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.repo.new.RoomRepository
import com.raritasolutions.mymining.repo.new.TeacherRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest


@Controller
class IndexController(val roomRepository: RoomRepository,
                      val teacherRepository: TeacherRepository) {

    private val logger = LoggerFactory.getLogger(IndexController::class.java)

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
        logger.info("Requested status of Spring. The status is up & $model")
        return ModelAndView("status", model)
    }

}
