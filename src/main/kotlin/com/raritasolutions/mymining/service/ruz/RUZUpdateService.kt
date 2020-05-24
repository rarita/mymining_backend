package com.raritasolutions.mymining.service.ruz

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.new.LessonType
import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.repo.new.NormalizedRepository
import com.raritasolutions.mymining.service.base.UpdateSource
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class RUZUpdateService(private val pairRepository: PairRepository,
                       private val normalizedRepository: NormalizedRepository,
                       private val ruzWebFetcher: RUZWebFetcher) : UpdateSource {

    private val logger = LoggerFactory.getLogger(RUZUpdateService::class.java)

    fun getTargetGroups(): List<String>
        = listOf("ИСТ-16", "ИАС-16")

    /**
     * Persists intermediate PairRecord classes
     * as normalized DB structure.
     */
    fun PairRecord.persistAsNormalizedModel() {
        // Find the group
        val groupId = normalizedRepository.findGroupIdByName(this.group)
        logger.info("group ID for ${this.group} in DB is $groupId")

        // Persist rooms and teachers and get IDs
        val roomIds = this.room
                .split(",\\s?".toRegex())
                .filter(String::isNotBlank)
                .map {
                    normalizedRepository.mergeRoom(it,  this.buildingID)
                    normalizedRepository.getRoomId(it, this.buildingID)
                }

        logger.info("Merged room ids: $roomIds")

        val teacherIds = this.teacher
                .split(",\\s?".toRegex())
                .map {
                    normalizedRepository.mergeTeacher(it)
                    normalizedRepository.getTeacherId(it)
                }

        logger.info("Merged teacher ids: $roomIds")

        val timeComponents = this.timeSpan
                .split("([.\\-])".toRegex())
                .filter(String::isNotBlank)
                .map { it.toInt() }

        val timeStart = LocalTime.of(timeComponents[0], timeComponents[1])

        val lessonTypeOrdinal = LessonType.values()
                .firstOrNull { it.repr == this.type }?.ordinal
                    ?: throw IllegalStateException("Invalid PairRecord type: ${this.type}")

        normalizedRepository.mergeLesson(
                this.day,
                this.one_half,
                this.over_week,
                this.subject,
                timeStart,
                lessonTypeOrdinal,
                this.week,
                groupId)

        val lessonId = normalizedRepository.getLessonId(
                this.day,
                this.one_half,
                this.over_week,
                this.subject,
                timeStart,
                lessonTypeOrdinal,
                this.week,
                groupId
        )

        logger.info("Lesson id: $lessonId")

        // Add Teachers and Rooms to JOIN-Tables

        for (roomId in roomIds)
            normalizedRepository.addRoomToLesson(roomId, lessonId)

        for (teacherId in teacherIds)
            normalizedRepository.addTeacherToLesson(teacherId, lessonId)

    }

    override fun update() {

        val date = LocalDate.of(2020, 2, 20)

        getTargetGroups().forEach {
            val sch = ruzWebFetcher.getScheduleForGroup(it, date) // todo delet this
            sch.forEach { it.persistAsNormalizedModel() }
        }

    }

}