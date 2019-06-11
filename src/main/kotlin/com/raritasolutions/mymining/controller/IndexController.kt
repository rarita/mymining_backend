package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.repo.PairRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import java.lang.management.ManagementFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest


@Controller
class IndexController @Autowired constructor(private val pairRepo: PairRepository)
{
    @GetMapping("/") fun index(requestParams: HttpServletRequest): ModelAndView {
        // Log connections to catch checkers
        println("[${ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)}]: " +
                "Got a connection from ${requestParams.remoteAddr} - " +
                requestParams.getHeader("User-Agent"))
        return ModelAndView("index_ajax")
    }

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
}
