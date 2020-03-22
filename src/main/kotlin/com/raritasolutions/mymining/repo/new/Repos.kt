package com.raritasolutions.mymining.repo.new

import com.raritasolutions.mymining.model.new.Batch
import com.raritasolutions.mymining.model.new.Room
import com.raritasolutions.mymining.model.new.Teacher
import org.springframework.data.repository.CrudRepository

interface BatchRepository : CrudRepository<Batch, Int>

interface RoomRepository : CrudRepository<Room, Int>

interface TeacherRepository : CrudRepository<Teacher, Int>