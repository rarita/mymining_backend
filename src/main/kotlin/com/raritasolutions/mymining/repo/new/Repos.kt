package com.raritasolutions.mymining.repo.new

import com.raritasolutions.mymining.model.new.Group
import com.raritasolutions.mymining.model.new.Lesson
import com.raritasolutions.mymining.model.new.Room
import com.raritasolutions.mymining.model.new.Teacher
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalTime
import javax.transaction.Transactional

interface NormalizedRepository: CrudRepository<Lesson, Int> {

    @Query("SELECT group_id from groups " +
            "WHERE group_name = :groupName " +
            "LIMIT 1", nativeQuery = true)
    fun findGroupIdByName(groupName: String): Int

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

interface GroupRepository : CrudRepository<Group, Int>

interface RoomRepository : CrudRepository<Room, Int>

interface TeacherRepository : CrudRepository<Teacher, Int>