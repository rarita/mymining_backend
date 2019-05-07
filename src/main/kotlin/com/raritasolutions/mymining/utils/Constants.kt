package com.raritasolutions.mymining.utils

import com.raritasolutions.mymining.model.reborn.RGBColor

// todo consider java.date or JodaTime to make locale-friendly weekdays
val DAYS_NAMES_MAP = mapOf(
        1 to "Понедельник",
        2 to "Вторник",
        3 to "Среда",
        4 to "Четверг",
        5 to "Пятница",
        6 to "Суббота"
)

// Needed for legacy stuff like TabulaConverter to work
val DAYS_ORDER_MAP = mapOf(
        "ПОНЕДЕЛЬНИК" to 1,
        "ВТОРНИК" to 2,
        "СРЕДА" to 3,
        "ЧЕТВЕРГ" to 4,
        "ПЯТНИЦА" to 5,
        "СУББОТА" to 6
)

// todo map it to time ranges
val TIMES_LIST = listOf(
        "8.50-10.20",
        "10.35-12.05",
        "12.35-14.05",
        "14.15-15.45",
        "15.55-17.20",
        "17.30-19.00",
        "19.10-20.30",
        "20.40-22.00"
)

val FIRST_TEN_ROMANS = listOf(
        "I",
        "II",
        "III",
        "IV",
        "V",
        "VI",
        "VII",
        "VIII",
        "IX",
        "X")

// Color constants
// TODO Hook it to the config file or something. These may change in the future
val BUILDINGS_CELL_COLOR = mapOf(
        1 to RGBColor(220, 230, 240),
        2 to RGBColor(255, 255, 204),
        3 to RGBColor(215, 227, 187)
)

val BUILDINGS_TEXT_COLOR = mapOf(
        1 to RGBColor(51, 51, 153),
        2 to RGBColor(255, 204, 0),
        3 to RGBColor(51, 153, 102)
)