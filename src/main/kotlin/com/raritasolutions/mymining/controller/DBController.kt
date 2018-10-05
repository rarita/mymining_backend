package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.fetcher.txtToPairRecordList
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.repo.PairRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView


@Controller
@RequestMapping("/db")
class DBController @Autowired constructor(val pairRepo: PairRepository){

    @GetMapping("/extract_local_txt")
    fun process(): ModelAndView
    {
        val pairsList
                = txtToPairRecordList("C:\\Users\\rarita\\Documents\\decompose_tables\\out_v2.txt")
        pairRepo.saveAll(pairsList)
        return ModelAndView("job_result", mapOf("caller" to "TXT2DB Pair Fetcher",
                                                    "message" to "Job Done, ${pairsList.size} lessons extracted and saved to repository."))
    }
    @GetMapping("list")
    fun getListInTable(@RequestParam(value = "group",required = false,defaultValue = "") group: String,
                       @RequestParam(value = "day", required = false, defaultValue = "0") day: Int): ModelAndView {
        val model = HashMap<String,Iterable<PairRecord>>()
        model["data"] = if ((day != 0) and (group.isNotBlank()))
            pairRepo.findByGroupAndDay(group,day)
        else
            pairRepo.findAll()
        return ModelAndView("pair_list",model)
    }
    @GetMapping("/list_json")
    @ResponseBody
    fun getAllPairs() = pairRepo.findAll()

}