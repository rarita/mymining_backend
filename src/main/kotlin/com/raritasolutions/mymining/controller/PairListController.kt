package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.repo.PairRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView

@Controller
class PairListController @Autowired constructor(val pairRepo: PairRepository){

    @GetMapping("list")
    fun getListInTable(): ModelAndView {
        val model = HashMap<String,Iterable<PairRecord>>()
        model["data"] = pairRepo.findAll()
        return ModelAndView("pair_list",model)
    }

    @GetMapping("/list_json")
    @ResponseBody
    fun getAllPairs() = pairRepo.findAll()

}