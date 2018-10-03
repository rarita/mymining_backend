package com.raritasolutions.mymining.utils

/* Cell extractor resources */
val pairTypesRegex = "(л/р|пр\\.)".toRegex()
val teacherRank = "(Доц.|Проф.|Асс.|Асп|Ст.пр.|Преп.)".toRegex()
val teacherInitials = "\\p{L}\\.\\p{L}\\.".toRegex()
// todo build this from 2 above
val teacherRegex = "(Доц.|Проф.|Асс.|Асп|Ст.пр.|Преп.)\\p{L}+\\.\\p{L}\\.".toRegex() // Careful!
val roomRegex = "No.+?(?=(No))".toRegex()
val weeksRegex = "(I+|ч/н)".toRegex()
val oneHalfRegex = "1/2".toRegex()
val lineBreaksRegex = "(\\r\\n|\\n)".toRegex()

/* CSV extractor resources */
val roomSearchingRegex = "No*".toRegex()
val pairNoRoomRegex = "$weeksRegex*?.*$teacherRegex.*$pairTypesRegex*?.*".toRegex()
val pairRegex = "$pairNoRoomRegex.*$roomSearchingRegex".toRegex()
val groupRegex = "\\p{Lu}{2,}-\\d{2,}.*".toRegex()

/* Raw extractor resources */
val multiplePairRegexVanilla = "I+.+I+.+".toRegex()
val ripVanillaRegex = "I.+? (?=II)".toRegex()
val multiplePairRegexOneHalf = "1/2.+1/2.+".toRegex()
val ripOneHalfRegex = "1/2.+? (?=1/2)".toRegex()
val multiplePairRegexOneLine = "I.*?-.*?No\\d{2,}.*?II.*?-.*?No\\d{2,}".toRegex()
val ripOneLineRegex = "I.+? (?=II)".toRegex()
