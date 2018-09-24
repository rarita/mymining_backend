package com.raritasolutions.mymining.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class IndexController
{
    @GetMapping("/") fun index(): ModelAndView {
        val model = HashMap<String,String>()
        model["name"] = "Rarita"
        return ModelAndView("index",model)
    }
}
