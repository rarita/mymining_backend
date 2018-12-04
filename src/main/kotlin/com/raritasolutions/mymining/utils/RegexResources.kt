package com.raritasolutions.mymining.utils

/* Cell extractor resources */
val contentInBracesRegex = "\\(.+?(?=\\))\\)".toRegex()
val redundantSymbolsRegex = "([-_])".toRegex()
val pairTypesRegex = "(л/р|пр\\.)".toRegex()
val teacherRank = "(Доц.|Проф.|Асс.|Асп|Ст.пр.|Преп.)".toRegex()
val teacherInitials = "\\p{L}\\.\\p{L}\\.".toRegex()
// todo build this from 2 above
val teacherRegex = "(Доц.|Проф.|Асс.|Асп|Ст.пр.|Преп.)\\p{L}+\\.\\p{L}\\.,*".toRegex() // Careful!
val roomRegex = "No.+?(?=(No))".toRegex()
val weeksRegex = "(I+|ч/н)".toRegex()
val oneHalfRegex = "1/[23]".toRegex()
val lineBreaksRegex = "(\\r\\n|\\n)".toRegex()
val timeSpanRegex = "\\d+\\.\\d{2}-\\d+\\.\\d{2}".toRegex()

/* CSV extractor resources */
val roomSearchingRegex = "No*".toRegex()
val pairNoRoomRegex = "$weeksRegex*?.*$teacherRegex.*$pairTypesRegex*?.*".toRegex()
val pairRegex = "$pairNoRoomRegex.*$roomSearchingRegex".toRegex()
val groupRegex = "\\p{Lu}{2,}-\\d{2,}.*".toRegex()

/* Raw extractor resources */
val multiweekRoomRegex = "I+.*?-.*?No.*?\\d{2,}.*?I+.*?-.*?No.*?\\d{2,}".toRegex()
val multiplePairRegexVanilla = "I+.+I+.+".toRegex()
val ripVanillaRegex = "I.+? (?=I\\s*I)".toRegex()
val multiplePairRegexOneHalf = "1/[23].+1/[23].+".toRegex()
val ripOneHalfRegex = "1/[23].+? (?=1/[23])".toRegex()
val multiplePairRegexOneLine = "I.*?-.*?(No)?\\d{2,}.*?I\\s*I.*?-.*?(No)?\\d{2,}".toRegex()
val ripOneLineRegex = "I.+? (?=I\\s*I)".toRegex()
