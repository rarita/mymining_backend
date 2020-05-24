package com.raritasolutions.mymining.service.ruz

import com.fasterxml.jackson.databind.ObjectMapper
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.service.ScheduleTimeService
import com.raritasolutions.mymining.utils.withMonday
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class RUZWebFetcher(private val okHttpClient: OkHttpClient,
                    private val objectMapper: ObjectMapper,
                    private val scheduleTimeService: ScheduleTimeService) {

    private fun getBaseURLBuilder() = HttpUrl.Builder()
            .scheme("https")
            .host("raspisanie.spmi.ru")

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    private fun HttpUrl.toGETRequest(): Request
        = Request.Builder()
            .url(this)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .get()
            .build()

    /**
     * Gets the specified group's unique identifier in the
     * RUZ service. Identifier is then used to look up that
     * group's schedule
     * @param group String representation of the target group
     * @return An integer ID of specified group
     */
    fun getGroupIdFromName(group: String): Int {
        // todo cache responses since ids aren't changing much

        val completeURL = getBaseURLBuilder().addEncodedPathSegments("api/search")
                .addQueryParameter("term", group)
                .addQueryParameter("type", "group")
                .build()

        val request = completeURL.toGETRequest()

        val response = okHttpClient.newCall(request).execute()
        val responseString = response.body?.string()
                ?: throw IllegalStateException("Response from request ${request.url} " +
                        "has a null body")

        val jsonNode = objectMapper.readTree(responseString)
        if (jsonNode.has("error"))
            throw IllegalStateException("Server responded with an error: ${jsonNode["error"].textValue()}")

        if (jsonNode.size() == 0)
            throw IllegalStateException("No matches for the $group query")

        return jsonNode[0]["id"].intValue()
    }

    /**
     * Fetch the json package of schedule data that
     * meets specified requirements from the RUZ service.
     * @param start date of the first day
     * @param end date of the last day (inclusive)
     * @param group String representation of needed group
     */
    fun getDataFromREST(start: LocalDate,
                        end: LocalDate,
                        group: String): String {

        val groupID = getGroupIdFromName(group)
        val completeURL = getBaseURLBuilder().addEncodedPathSegments("api/schedule/group")
                .addPathSegment(groupID.toString())
                .addEncodedQueryParameter("start", start.format(timeFormatter))
                .addEncodedQueryParameter("finish", end.format(timeFormatter))
                .build()

        val request = completeURL.toGETRequest()
        val response = okHttpClient.newCall(request).execute()

        return response.body?.string()
                ?: throw IllegalStateException("No body on response")
    }

    /**
     * Get the list of pairs for the specified week
     * and group. Dates that would be requested from the server
     * is either this week or the next week depending on the params.
     * @param week Integer representation of odd/even week. Must be 1 or 2.
     * @param group String representation of the target student group.
     * @param baseDate Date to perform the search from
     * @return List of [PairRecord] that meets specified requirements
     */
    fun getScheduleForGroupAndWeek(week: Int,
                                   group: String,
                                   baseDate: LocalDate): List<PairRecord> {

        val tDate = if (scheduleTimeService.getCurrentWeek() == week.toLong())
            baseDate.withMonday()
        else
            baseDate.plusWeeks(1).withMonday()

        val responseString
                = getDataFromREST(tDate,
                                  tDate.plusDays(6),
                                  group)

        return objectMapper.readValue(responseString, Array<PairRecord>::class.java).toList()
    }

    /**
     * Get all the pairs for the specified group.
     * Fetches pairs for two weeks separately, then
     * merges it and returns the result.
     * @param group String representation of the target student group.
     * @param date Date of schedule to get (only calendar week matters)
     * @return Set of [PairRecord] for the target student group
     */
    fun getScheduleForGroup(group: String, date: LocalDate): Set<PairRecord> {
        val rawWeekSchedule
                = Pair(getScheduleForGroupAndWeek(1, group, date),
                       getScheduleForGroupAndWeek(2, group, date))

        // An intersection means that the week is equal to zero
        val intersection = rawWeekSchedule.first.intersect(rawWeekSchedule.second)

        val sortedWeekSchedule
                = Pair(rawWeekSchedule.first.minus(intersection).apply { forEach{it.week = 1 }},
                       rawWeekSchedule.second.minus(intersection).apply { forEach{it.week = 2 }})


        return (sortedWeekSchedule.first + sortedWeekSchedule.second + intersection).toSet()
    }

}