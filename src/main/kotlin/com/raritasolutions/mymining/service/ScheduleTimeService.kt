package com.raritasolutions.mymining.service

import com.raritasolutions.mymining.utils.withMonday
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit

@Service
class ScheduleTimeService {

    fun getCurrentWeek(now: LocalDate = LocalDate.now()): Long {

        val semesterStart =
                if (now.month > Month.JUNE) // If it is in first semester
                    now
                            .withMonth(9)
                            .withDayOfMonth(1)
                else
                    now
                            .withMonth(2)
                            .withDayOfMonth(3)

        val weekend = if (now.dayOfWeek > DayOfWeek.FRIDAY) 1 else 0

        // Calculate the week difference between today and semester start
        return (((ChronoUnit.WEEKS.between(semesterStart.withMonday(), now.withMonday()) + weekend) % 2) + 1)
    }

    fun getCurrentDay()
            = with (LocalDate.now().dayOfWeek.value) {
                if (this <= 5) this else 1
            }

}