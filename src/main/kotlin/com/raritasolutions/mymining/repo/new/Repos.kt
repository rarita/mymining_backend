package com.raritasolutions.mymining.repo.new

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.new.Group
import com.raritasolutions.mymining.model.new.Lesson
import com.raritasolutions.mymining.model.new.Room
import com.raritasolutions.mymining.model.new.Teacher
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalTime
import javax.transaction.Transactional

interface GroupName { var group: String }
interface Teacher {var teacher: String }

interface NormalizedRepository: CrudRepository<Lesson, Int> {

    @Query("SELECT group_id from groups " +
            "WHERE group_name = :groupName " +
            "LIMIT 1", nativeQuery = true)
    fun findGroupIdByName(groupName: String): Int

    @Query("SELECT group_name from groups;",
            nativeQuery = true)
    fun findAllGroups(): Set<String>

    @Query("SELECT group_name as group from groups " +
            "WHERE group_name ILIKE %:group% " +
            "ORDER BY group_name DESC " +
            "LIMIT 3;", nativeQuery = true)
    fun findGroupsLike(group: String): Set<GroupName>

    @Query("SELECT teacher_alias as teacher from teachers " +
            "WHERE teacher_alias ILIKE %:teacherName% " +
            "LIMIT 3;", nativeQuery = true)
    fun findTeachersLike(teacherName: String): Set<com.raritasolutions.mymining.repo.new.Teacher>

    @Modifying
    @Transactional
    @Query("INSERT INTO groups(group_name) " +
            "values (:groupName)" +
            "ON CONFLICT DO NOTHING;" , nativeQuery = true)
    fun mergeGroup(groupName: String)

    @Modifying
    @Transactional
    @Query("INSERT INTO teachers(teacher_alias, teacher_link, teacher_full_name, teacher_rank, teacher_photo) " +
            "values (:alias, null, null, null, null)" +
            "ON CONFLICT DO NOTHING;" , nativeQuery = true)
    fun mergeTeacher(alias: String)

    @Query("SELECT teacher_id as tid from teachers where teacher_alias = :alias ;", nativeQuery = true)
    fun getTeacherId(alias: String): Int

    @Modifying
    @Transactional
    @Query("INSERT INTO rooms(building_id, room_name) " +
            "VALUES (:buildingId, :roomName) " +
            "ON CONFLICT DO NOTHING;", nativeQuery = true)
    fun mergeRoom(roomName: String, buildingId: Int)

    @Query("SELECT room_id as rid from rooms where room_name = :roomName and building_id = :buildingId ;",
        nativeQuery = true)
    fun getRoomId(roomName: String, buildingId: Int): Int

    @Modifying
    @Transactional
    @Query("INSERT INTO lessons(day, one_half, over_week, subject, time_start, type, week, group_id) " +
            "VALUES (:day, :oneHalf, :overWeek, :subject, :timeStart, :type, :week, :groupId) " +
            "ON CONFLICT DO NOTHING;", nativeQuery = true)
    fun mergeLesson(day:Int,
                    oneHalf: String,
                    overWeek: Boolean,
                    subject: String,
                    timeStart: LocalTime,
                    type: Int,
                    week: Int,
                    groupId: Int)

    @Query("SELECT class_id from lessons " +
            "WHERE day = :day AND " +
            "one_half = :oneHalf AND " +
            "group_id = :groupId AND " +
            "over_week = :overWeek AND " +
            "time_start = :timeStart AND " +
            "subject = :subject AND " +
            "type = :type AND " +
            "week = :week " +
            "LIMIT 1;", nativeQuery = true)
    fun getLessonId(day:Int,
                    oneHalf: String,
                    overWeek: Boolean,
                    subject: String,
                    timeStart: LocalTime,
                    type: Int,
                    week: Int,
                    groupId: Int): Int

    @Modifying
    @Transactional
    @Query("INSERT INTO lesson_teacher(lesson_id, teacher_id) " +
            "values (:lessonId, :teacherId) " +
            "ON CONFLICT DO NOTHING;", nativeQuery = true)
    fun addTeacherToLesson(teacherId: Int, lessonId: Int)

    @Modifying
    @Transactional
    @Query("INSERT INTO lesson_room(lesson_id, room_id) " +
            "values (:lessonId, :roomId) " +
            "ON CONFLICT DO NOTHING;", nativeQuery = true)
    fun addRoomToLesson(roomId: Int, lessonId: Int)

}

/**
 * Returns intermediate values represented by the PairRecord class
 */
interface IntermediateRepository: CrudRepository<PairRecord, Int> {
    @Query("SELECT class_id as id, " +
            "       group_name as _group, " +
            "       teacher_alias as teacher, " +
            "       week, " +
            "       day, " +
            "       concat(to_char(time_start, 'HH.MI'), '-', to_char(time_start + CAST(time '01:30' as interval), 'HH.MI')) as time_span, " +
            "       subject, " +
            "       room_name as room, " +
            "       one_half, " +
            "       over_week, " +
            "       building_id as buildingId, " +
            "       CASE lessons.type " +
            "           WHEN 0 THEN 'лекция' " +
            "           WHEN 1 THEN 'практика' " +
            "           WHEN 2 THEN 'лабораторная работа' " +
            "           WHEN 3 THEN 'экзамен' " +
            "           WHEN 4 THEN 'занятие' " +
            "           END as type," +
            "       false as locked from lessons " +
            "INNER JOIN groups on (lessons.group_id = groups.group_id) " +
            "INNER JOIN lesson_room on (lessons.class_id = lesson_room.lesson_id) " +
            "INNER JOIN rooms on (lesson_room.room_id = rooms.room_id) " +
            "INNER JOIN lesson_teacher lt on lessons.class_id = lt.lesson_id " +
            "INNER JOIN teachers t on lt.teacher_id = t.teacher_id;", nativeQuery = true)
    fun findAllPairRecords(): Set<PairRecord>

    @Query("SELECT class_id as id, " +
            "       group_name as _group, " +
            "       teacher_alias as teacher, " +
            "       week, " +
            "       day, " +
            "       concat(to_char(time_start, 'HH24.MI'), '-', to_char(time_start + CAST(time '01:30' as interval), 'HH24.MI')) as time_span, " +
            "       subject, " +
            "       room_name as room, " +
            "       one_half, " +
            "       over_week, " +
            "       building_id as buildingId, " +
            "       CASE lessons.type " +
            "           WHEN 0 THEN 'лекция' " +
            "           WHEN 1 THEN 'практика' " +
            "           WHEN 2 THEN 'лабораторная работа' " +
            "           WHEN 3 THEN 'экзамен' " +
            "           WHEN 4 THEN 'занятие' " +
            "           END as type," +
            "       false as locked from lessons " +
            "INNER JOIN groups on (lessons.group_id = groups.group_id) " +
            "INNER JOIN lesson_room on (lessons.class_id = lesson_room.lesson_id) " +
            "INNER JOIN rooms on (lesson_room.room_id = rooms.room_id) " +
            "INNER JOIN lesson_teacher lt on lessons.class_id = lt.lesson_id " +
            "INNER JOIN teachers t on lt.teacher_id = t.teacher_id " +
            "where groups.group_name = :groupName ;", nativeQuery = true)
    fun findAllPairRecordsByGroup(groupName: String): Set<PairRecord>

    @Query("SELECT class_id as id, " +
            "       group_name as _group, " +
            "       teacher_alias as teacher, " +
            "       week, " +
            "       day, " +
            "       concat(to_char(time_start, 'HH24.MI'), '-', to_char(time_start + CAST(time '01:30' as interval), 'HH24.MI')) as time_span, " +
            "       subject, " +
            "       room_name as room, " +
            "       one_half, " +
            "       over_week, " +
            "       building_id as buildingId, " +
            "       CASE lessons.type " +
            "           WHEN 0 THEN 'лекция' " +
            "           WHEN 1 THEN 'практика' " +
            "           WHEN 2 THEN 'лабораторная работа' " +
            "           WHEN 3 THEN 'экзамен' " +
            "           WHEN 4 THEN 'занятие' " +
            "           END as type," +
            "       false as locked from lessons " +
            "INNER JOIN groups on (lessons.group_id = groups.group_id) " +
            "INNER JOIN lesson_room on (lessons.class_id = lesson_room.lesson_id) " +
            "INNER JOIN rooms on (lesson_room.room_id = rooms.room_id) " +
            "INNER JOIN lesson_teacher lt on lessons.class_id = lt.lesson_id " +
            "INNER JOIN teachers t on lt.teacher_id = t.teacher_id " +
            "where t.teacher_alias = :teacherName ;", nativeQuery = true)
    fun findAllPairRecordsByTeacher(teacherName: String): Set<PairRecord>

    @Query("SELECT class_id as id, " +
            "       group_name as _group, " +
            "       teacher_alias as teacher, " +
            "       week, " +
            "       day, " +
            "       concat(to_char(time_start, 'HH24.MI'), '-', to_char(time_start + CAST(time '01:30' as interval), 'HH24.MI')) as time_span, " +
            "       subject, " +
            "       room_name as room, " +
            "       one_half, " +
            "       over_week, " +
            "       building_id as buildingId, " +
            "       CASE lessons.type " +
            "           WHEN 0 THEN 'лекция' " +
            "           WHEN 1 THEN 'практика' " +
            "           WHEN 2 THEN 'лабораторная работа' " +
            "           WHEN 3 THEN 'экзамен' " +
            "           WHEN 4 THEN 'занятие' " +
            "           END as type," +
            "       false as locked from lessons " +
            "INNER JOIN groups on (lessons.group_id = groups.group_id) " +
            "INNER JOIN lesson_room on (lessons.class_id = lesson_room.lesson_id) " +
            "INNER JOIN rooms on (lesson_room.room_id = rooms.room_id) " +
            "INNER JOIN lesson_teacher lt on lessons.class_id = lt.lesson_id " +
            "INNER JOIN teachers t on lt.teacher_id = t.teacher_id " +
            "where rooms.room_name = :roomName ;", nativeQuery = true)
    fun findAllPairRecordsByRoom(roomName: String): Set<PairRecord>

    @Query("SELECT class_id as id, " +
            "       group_name as _group, " +
            "       teacher_alias as teacher, " +
            "       week, " +
            "       day, " +
            "       concat(to_char(time_start, 'HH24.MI'), '-', to_char(time_start + CAST(time '01:30' as interval), 'HH24.MI')) as time_span, " +
            "       subject, " +
            "       room_name as room, " +
            "       one_half, " +
            "       over_week, " +
            "       building_id as buildingId, " +
            "       CASE lessons.type " +
            "           WHEN 0 THEN 'лекция' " +
            "           WHEN 1 THEN 'практика' " +
            "           WHEN 2 THEN 'лабораторная работа' " +
            "           WHEN 3 THEN 'экзамен' " +
            "           WHEN 4 THEN 'занятие' " +
            "           END as type," +
            "       false as locked from lessons " +
            "INNER JOIN groups on (lessons.group_id = groups.group_id) " +
            "INNER JOIN lesson_room on (lessons.class_id = lesson_room.lesson_id) " +
            "INNER JOIN rooms on (lesson_room.room_id = rooms.room_id) " +
            "INNER JOIN lesson_teacher lt on lessons.class_id = lt.lesson_id " +
            "INNER JOIN teachers t on lt.teacher_id = t.teacher_id " +
            "where groups.group_name = :groupName AND day = :day ;", nativeQuery = true)
    fun findAllPairRecordsByGroupAndDay(groupName: String, day: Int): Set<PairRecord>

}

interface GroupRepository : CrudRepository<Group, Int>

interface RoomRepository : CrudRepository<Room, Int>

interface TeacherRepository : CrudRepository<Teacher, Int>