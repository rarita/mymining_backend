package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.composer.DayTimeScheduleComposer
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.isCorrect
import com.raritasolutions.mymining.model.toPairViewModel
import com.raritasolutions.mymining.model.viewmodel.BaseViewModel
import com.raritasolutions.mymining.model.viewmodel.DayHeaderModel
import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.repo.new.IntermediateRepository
import com.raritasolutions.mymining.service.LegacyUpdateService
import com.raritasolutions.mymining.service.RebornUpdateService
import com.raritasolutions.mymining.service.WebUpdateService
import com.raritasolutions.mymining.service.base.BaseUpdateService
import com.raritasolutions.mymining.service.base.UpdateSource
import com.raritasolutions.mymining.service.ruz.LKAuthService
import com.raritasolutions.mymining.service.ruz.LKGroupFetcher
import com.raritasolutions.mymining.service.ruz.RUZUpdateService
import com.raritasolutions.mymining.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView

@Controller
@RequestMapping("/db")
class DBController @Autowired constructor(private val pairRepo: PairRepository,
                                          private val intermediateRepository: IntermediateRepository,
                                          private val dtsc : DayTimeScheduleComposer,
                                          private val updateService: WebUpdateService,
                                          private val legacyUpdateService : LegacyUpdateService,
                                          private val rebornUpdateService: RebornUpdateService,
                                          private val ruzUpdateService: RUZUpdateService,
                                          private val lkGroupFetcher: LKGroupFetcher,
                                          private val lkAuthService: LKAuthService) {

    @GetMapping("/extract")
    fun extract(@RequestParam(value = "type",required = false, defaultValue = "ruz") type: String)
            : ModelAndView {
        val service : UpdateSource = when (type) {
            "legacy" -> legacyUpdateService
            "tabula" -> updateService
            "reborn" -> rebornUpdateService
            "ruz" -> ruzUpdateService
            else -> throw IllegalArgumentException("Supplied type parameter is illegal. " +
                    "No extractors matching $type")
        }

        service.update()

        return ModelAndView("job_result", mapOf(
                "caller" to "Remote Extractor",
                "message" to "List of errors occurred while extracting\n" +
                        if (service is BaseUpdateService)
                            service.report.toString()
                        else
                            "can be found in application logs"))
    }

    @GetMapping("/check")
    fun checkCorrectness(@RequestParam(name = "datasource", required = false, defaultValue = "legacy") dataSource: String)
            : ModelAndView {
        val source = when (dataSource) {
            "legacy" -> pairRepo.findAll()
            "normalized" -> intermediateRepository.findAllPairRecords()
            else -> throw IllegalArgumentException("No such data source: $dataSource")
        }
        val errors = source
                .filterNot { it.isCorrect() }
                .withIndex()
                .joinToString(separator = "\n") { "[${it.index + 1}]: ${it.value} might be faulty." }
        return ModelAndView("job_result", mapOf("caller" to "Error Checker",
                "message" to "List of extraction errors found in DB\n$errors\nTotal pairs processed: ${source.count()}"))
    }

    @GetMapping("fetch_groups")
    @ResponseBody
    fun fetchGroups(@RequestParam(value = "sessionId", required = true)
                    sessionId: String): String {
        lkGroupFetcher.loadGroups(sessionId)
        return "OK"
    }

    @GetMapping("authenticate")
    @ResponseBody
    fun lkAuthenticate()
        = lkAuthService.authenticate()

    @GetMapping("list")
    fun getListInTable(@RequestParam(value = "group",required = false,defaultValue = "") group: String,
                       @RequestParam(value = "day", required = false, defaultValue = "0") day: Int,
                       @RequestParam(value = "datasource", required = false, defaultValue = "normalized")
                       dataSource: String): ModelAndView {

        val model = HashMap<String,Iterable<PairRecord>>()
        model["data"] =
                when(dataSource) {
                    "legacy" -> if ((day != 0) and (group.isNotBlank()))
                                    pairRepo.findByGroupAndDay(group, day)
                                else
                                    pairRepo.findAll()
                    "normalized" -> if ((day != 0) and (group.isNotBlank()))
                                        intermediateRepository.findAllPairRecordsByGroupAndDay(group, day)
                                    else
                                        intermediateRepository.findAllPairRecords()
                    else -> listOf()
                }
        return ModelAndView("pair_list", model)
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
    fun getGenericSchedule(@RequestParam(value="room",required = false) room: String?,
                           @RequestParam(value="teacher",required = false) teacher: String?,
                           @RequestParam(value="group",required = false) group: String?,
                           @RequestParam(value ="simplified",required = false) simplified: Boolean?) : ModelAndView
        =   when {
                room != null -> dtsc.compose(if (simplified == true) PairRecord::formatSimpleTeacher else PairRecord::formatSoloGroup, makeCaption(room)) { room in it.room }
                teacher != null -> dtsc.compose(if (simplified == true) PairRecord::formatSimpleRoom else PairRecord::formatSoloRoom, makeCaption(teacher)) { teacher in it.teacher }
                group != null -> dtsc.compose(PairRecord::formatSoloGeneric, makeCaption(group)) { it.group.matchesSubgroup(group) }
                else -> ModelAndView("job_failed", mapOf("error" to "invalid call of /gen_sch"))
            }

    /* All params are required because request is being sent via form and
     * all of the listed params are present in any case */
    @GetMapping("/feed")
    fun getFeedSchedule(@RequestParam(value = "group", required = true) group: String,
                        @RequestParam(value = "week", required = true) week: Int,
                        @RequestParam(value = "day", required = true) day : Int) : ModelAndView{
        val feed = arrayListOf<BaseViewModel>()
        val days = if (day == 0) (1..5) else listOf(day)
        for (dayIter: Int in days) {
            feed += DayHeaderModel(DAYS_NAMES_MAP.getValue(dayIter))
            feed += (if (group.endsWith('а'))
                        findPairs(group.substringBeforeLast('а'), dayIter, week) + findPairs(group, dayIter, week)
                    else
                        findPairs(group, dayIter, week)).sortedBy { it.timeStart }
        }
        return ModelAndView("gen_feed", mapOf("feed" to feed))
    }

    private fun findPairs(group: String, day: Int, week: Int)
        = if (week != 0)
    /* Week = 0 -> select everything;
       Week = 1 or 2 -> select everything but opposing week */
        pairRepo.findByGroupAndDayAndWeekNot(group, day, if (week == 1) 2 else 1)
                .map(PairRecord::toPairViewModel)
    else
        pairRepo.findByGroupAndDay(group, day)
                .map(PairRecord::toPairViewModel)

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