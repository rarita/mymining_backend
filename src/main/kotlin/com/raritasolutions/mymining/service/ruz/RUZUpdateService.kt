package com.raritasolutions.mymining.service.ruz

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.new.LessonType
import com.raritasolutions.mymining.repo.new.NormalizedRepository
import com.raritasolutions.mymining.service.base.UpdateSource
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class RUZUpdateService(private val normalizedRepository: NormalizedRepository,
                       private val ruzWebFetcher: RUZWebFetcher) : UpdateSource {

    private val logger = LoggerFactory.getLogger(RUZUpdateService::class.java)

    val targetGroups
        get() = normalizedRepository.findAllGroups()

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
                }.toSet()

        logger.info("Merged room ids: $roomIds")

        val teacherIds = this.teacher
                .split(",\\s?".toRegex())
                .map {
                    normalizedRepository.mergeTeacher(it)
                    normalizedRepository.getTeacherId(it)
                }.toSet()

        logger.info("Merged teacher ids: $roomIds")

        val timeComponents = this.timeSpan
                .split("([.\\-])".toRegex())
                .filter(String::isNotBlank)
                .map { it.toInt() }

        val timeStart = LocalTime.of(timeComponents[0], timeComponents[1])

        // If it has more than 1 type create identical records with different types
        this.type.split(",\\s?".toRegex()).forEach { _type ->

            val lessonTypeOrdinal = LessonType.fromString(_type).ordinal
            storeLessonInDB(this, timeStart, lessonTypeOrdinal, groupId, roomIds, teacherIds)

        }

    }

    /**
     * A service should probably be created to do INSERT-SELECT-RETURN ID
     * sequence in the future. This method would be moved there too.
     */
    fun storeLessonInDB(basePair: PairRecord,
                        timeStart: LocalTime,
                        lessonTypeOrdinal: Int,
                        groupId: Int,
                        roomIds: Set<Int>,
                        teacherIds: Set<Int>) {

        normalizedRepository.mergeLesson(
                basePair.day,
                basePair.one_half,
                basePair.over_week,
                basePair.subject,
                timeStart,
                lessonTypeOrdinal,
                basePair.week,
                groupId)

        val lessonId = normalizedRepository.getLessonId(
                basePair.day,
                basePair.one_half,
                basePair.over_week,
                basePair.subject,
                timeStart,
                lessonTypeOrdinal,
                basePair.week,
                groupId)

        logger.info("Lesson id: $lessonId")

        // Add Teachers and Rooms to JOIN-Tables

        for (roomId in roomIds)
            normalizedRepository.addRoomToLesson(roomId, lessonId)

        for (teacherId in teacherIds)
            normalizedRepository.addTeacherToLesson(teacherId, lessonId)

    }

    override fun update() {

        logger.info("Querying RUZ update for ${targetGroups.size} groups...")
        val date = LocalDate.of(2020, 2, 20)

        targetGroups.forEach {
            logger.info("Requesting RUZ schedule for group $it")
            val sch = ruzWebFetcher.getScheduleForGroup(it, date) // todo delet this
            logger.info("Got ${sch.size} pairs for group $it, saving...")
            sch.forEach { it.persistAsNormalizedModel() }
        }

    }

}