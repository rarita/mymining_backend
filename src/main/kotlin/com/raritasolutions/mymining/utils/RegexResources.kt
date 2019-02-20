package com.raritasolutions.mymining.utils

/* Cell extractor resources */
val contentInBracesRegex = "\\(.+?(?=\\))\\)".toRegex()
val redundantSymbolsRegex = "(_)".toRegex()
val pairTypesRegex = "(л/р|пр\\.)".toRegex()
val endsWithPracticeRegex = ".*п.?р.?\\.?\$".toRegex() // For the tear down stage.
val teacherRankNormalCase = "(Доц.|Проф.|Асс.|Асп.|Ст.пр.|Преп.)".toRegex()
val teacherRank = "($teacherRankNormalCase|${teacherRankNormalCase.toString().toUpperCase()})".toRegex()
val teacherInitials = "\\p{L}\\.\\p{L}\\.".toRegex() // Note that this is only INITIALS, not whole teacher name regex.
val teacherInitialsNoClosingDot = "\\p{L}\\.\\p{L}".toRegex()

val subGroupRegex = "гр\\.[А-Я]{2,}.*?(?=а)а".toRegex() // todo consider improving this
/* Check case if beautiful people of the planet Earth who make this schedule
   Accidentally forgot to fill in teacher and just left default "Вакансия" on that field */
val teacherRegex = "($teacherRank\\p{L}+\\.\\p{L}\\.*,*|Вакансия)".toRegex() // Careful!
val roomRegex = "(I-)?No-?.+?(?=(No))".toRegex() // Check for "I-" before first room token to grab it from original string/
val weeksRegex = "(I+)".toRegex()
val oneHalfRegex = "[123]/[23]".toRegex()
val overWeekRegex = "ч/н".toRegex()
val lineBreaksRegex = "(\\r\\n|\\n)".toRegex()
val timeSpanRegex = "\\d+\\.\\d{2}-\\d+\\.\\d{2}".toRegex()

/* CSV extractor resources */
val roomSearchingRegex = "(No)+".toRegex()
val meaningfulTokensRegex = "($weeksRegex|$pairTypesRegex|$oneHalfRegex|$overWeekRegex)".toRegex()
val pairNoTeacherRegex = ".*$meaningfulTokensRegex*.*$roomSearchingRegex.*$meaningfulTokensRegex*.*".toRegex()
val pairNoRoomRegex = "$weeksRegex*.*$teacherRegex.*$pairTypesRegex*.*".toRegex()
val pairRegex = "$pairNoRoomRegex.*$roomSearchingRegex".toRegex()
val groupRegex = "\\p{Lu}{2,}-\\d{2,}.*".toRegex()

/* Pair splitter resources */
val improvedSubGroupRegex = "г.?р.?\\..?[А-Я].?[А-Я].+?(?=а)а".toRegex()
val oneHalfTokenRegex = ".*[12].?/.?[23].*?(?=((I+|ч.?/.?н|$improvedSubGroupRegex)*.[12].?/.?[23]))".toRegex()
val weekTokenRegex = ".*?(?>I+|ч.?/.?н).*?(?=([12].?/.?[23]|ч.?/.?н|$improvedSubGroupRegex)*.?(I+))".toRegex() // .*((I.*)+|ч.*/.*н).*?(?=(1/\d)*.((I.*)+|ч.*/.*н)) ".*((I).+|ч.?/.?н).*?(?=((гр\\..?[А-Я]{2,}.*?(?=а)а)?.?[12].?/.?[23])*.((I)+|ч.?/.?н))"

/* Raw extractor resources */
val multiweekRoomRegex = "I+.*?-.*?No.*?\\d{2,}.*?I+.*?-.*?No.*?\\d{2,}".toRegex()
val multiplePairRegexVanilla = "I+.+I+.+".toRegex()
val ripVanillaRegex = "I.+? (?=I\\s*I)".toRegex()
val multiplePairRegexOneHalf = "1/[23].+1/[23].+".toRegex()
val ripOneHalfRegex = "1/[23].+? (?=1/[23])".toRegex()
val multiplePairRegexOneLine = "I.*?-.*?(No)?\\d{2,}.*?I\\s*I.*?-.*?(No)?\\d{2,}".toRegex()
val ripOneLineRegex = "I.+? (?=I\\s*I)".toRegex()
