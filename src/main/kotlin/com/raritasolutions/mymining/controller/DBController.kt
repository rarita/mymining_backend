package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.composer.DayTimeScheduleComposer
import com.raritasolutions.mymining.converter.BaseConverter
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.isCorrect
import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.service.LegacyUpdateService
import com.raritasolutions.mymining.service.WebUpdateService
import com.raritasolutions.mymining.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView

@Controller
@RequestMapping("/db")
class DBController @Autowired constructor(private val pairRepo: PairRepository,
                                          private val dtsc : DayTimeScheduleComposer,
                                          private val updateService: WebUpdateService,
                                          private val legacyUpdateService : LegacyUpdateService) {

    @GetMapping("/extract_legacy")
    fun process(): ModelAndView
    {
        legacyUpdateService.update()
        return ModelAndView("job_result", mapOf("caller" to "TXT2DB Pair Fetcher",
                "message" to "List of errors occurred while extracting\n" + legacyUpdateService.report.toString()))
    }

    @GetMapping("/extract_remote")
    fun extract(): ModelAndView {
        updateService.update()
        return ModelAndView("job_result", mapOf(
                "caller" to "Remote Extractor",
                "message" to "List of errors occurred while extracting\n" + updateService.report.toString()))
    }

    @GetMapping("/check")
    fun checkCorrectness(): ModelAndView {
        val errors = pairRepo
                .findAll()
                .filterNot { it.isCorrect() }
                .withIndex()
                .joinToString(separator = "\n") { "[${it.index + 1}]: ${it.value} might be faulty." }
        return ModelAndView("job_result", mapOf("caller" to "Error Checker",
                "message" to "List of extraction errors found in DB\n$errors"))
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

    // Should output the same as controller above
    @GetMapping("generic_list")
    fun getGenericList(): ModelAndView {
        val model = mutableMapOf<String, Iterable<*>>()
        model["data"] = pairRepo.findAll()
        model["columns"] = PairRecord().listOfProperties()
        return ModelAndView("generic_list",model)
    }

    @GetMapping("gen_sch")
    fun getRoomSchedule(@RequestParam(value="room",required = false) room: String?,
                        @RequestParam(value="teacher",required = false) teacher: String?,
                        @RequestParam(value="group",required = false) group: String?) : ModelAndView
        =   when {
                room != null -> dtsc.compose(PairRecord::formatSoloGroup) { it.room == room }
                teacher != null -> dtsc.compose(PairRecord::formatSoloRoom) { it.teacher == teacher }
                group != null -> dtsc.compose(PairRecord::formatSoloGeneric) { it.group == group }
                else -> ModelAndView("job_failed", mapOf("error" to "invalid call of /gen_sch"))
            }




    @GetMapping("/list_json")
    @ResponseBody
    fun getAllPairs() = pairRepo.findAll()


    @GetMapping("/edit")
    fun editEntry(@RequestParam(value = "id",required = true) entryId: Int): ModelAndView{
        if (!pairRepo.findById(entryId).isPresent)
            return ModelAndView("job_failed", mapOf("error" to "Record with id $entryId could not be found"))
        val recipient = pairRepo.findById(entryId).get()
        val model = mapOf("pair" to recipient)
        return ModelAndView("pair_editor", model)
    }

    @PostMapping("/edit")
    fun updateEntry(@ModelAttribute pair: PairRecord): RedirectView {
        pairRepo.save(pair)
        // todo might want to pass some params with it
        return RedirectView("/db/list")
    }
}