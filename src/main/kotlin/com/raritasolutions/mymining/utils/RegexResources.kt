package com.raritasolutions.mymining.utils

val pairTypesRegex = "(л/р|пр\\.)".toRegex()
val teacherRank = "(Доц.|Проф.|Асс.|Асп|Ст.пр.)".toRegex()
val teacherInitials = "\\p{L}\\.\\p{L}\\.".toRegex()
// todo build this from 2 above
val teacherRegex = "(Доц.|Проф.|Асс.|Асп|Ст.пр.)\\p{L}+\\.\\p{L}\\.".toRegex() // Careful!
val roomRegex = "No[\\d{3,}]+".toRegex()
val weeksRegex = "(I+|ч/н)".toRegex()
val oneHalfRegex = "1/2".toRegex()
val lineBreaksRegex = "(\\r\\n|\\n)".toRegex()


val pairRegex = "$weeksRegex*.*$teacherRegex.*$roomRegex".toRegex()

