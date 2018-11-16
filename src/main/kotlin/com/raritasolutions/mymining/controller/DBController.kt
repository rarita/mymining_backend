package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.fetcher.txtToPairRecordList
import com.raritasolutions.mymining.model.BasePairFormat
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1


@Controller
@RequestMapping("/db")
class DBController @Autowired constructor(val pairRepo: PairRepository){

    @GetMapping("/extract_local_txt")
    fun process(): ModelAndView
    {
        val pairsList
                = txtToPairRecordList("parsed.txt")
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

    // Should output the same as controller above
    @GetMapping("generic_list")
    fun getGenericList(): ModelAndView {
        val model = mutableMapOf<String, Iterable<*>>()
        model["data"] = pairRepo.findAll()
        model["columns"] = PairRecord().listOfProperties()
        return ModelAndView("generic_list",model)
    }

    fun produceDayTimeSchedule(foldingField: KMutableProperty1<PairRecord, String>? = null, equalConditions: Array<String>, repr: PairRecord.() -> String, cond: (PairRecord) -> Boolean): ModelAndView
    {
        val viewModel = ModelAndView("generic_list")
        val model = viewModel.model
        val pairsInRoom = pairRepo.findAll().filter(cond)
        val times = listOf("8.50-10.20","10.35-12.05","12.35-14.05","14.15-15.45","15.55-17.20")
        val days = listOf(1,2,3,4,5)
        model["columns"] = listOf("") + days
        model["rows"] = times
        model["data"] = mutableListOf<List<String>>()
        // rows are times cols are days
        for (time in times){
            val pairsAtGivenTime = pairsInRoom.filter { it.timeSpan == time }
            val rowRecords = mutableListOf(time)
            for (day in days){
                val pairsAtGivenDay = pairsAtGivenTime.filter { it.day == day }
                if (pairsAtGivenDay.isEmpty())
                    rowRecords += ""
                else
                    rowRecords += pairsAtGivenDay.formatGroupSchedule(equalConditions,foldingField,repr)
            }
            (model["data"] as MutableList<List<String>>).add(rowRecords)
        }
        return viewModel
    }

    @GetMapping("gen_sch")
    fun getRoomSchedule(@RequestParam(value="room",required = false) room: String?,
                        @RequestParam(value="teacher",required = false) teacher: String?,
                        @RequestParam(value="group",required = false) group: String?) : ModelAndView
        =   when {
                room != null -> produceDayTimeSchedule(PairRecord::group, arrayOf("subject","one_half","week","teacher"), PairRecord::formatSoloGroup) { it.room == room }
                teacher != null -> produceDayTimeSchedule(PairRecord::group, arrayOf("subject","week","one_half","room"), PairRecord::formatSoloRoom) { it.teacher == teacher }
                group != null -> produceDayTimeSchedule(null, arrayOf("subject","one_half","week","teacher") ,  PairRecord::formatSoloGeneric) { it.group == group }
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
        val model = mapOf(
                "caller" to "Editor: Update",
                "message" to "Updated a record @ id ${pair.id}")
        // todo might want to pass some params with it
        return RedirectView("/db/list")
    }
}