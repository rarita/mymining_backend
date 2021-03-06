package com.raritasolutions.mymining.utils

/* Cell extractor resources */
val contentInBracesRegex = "\\(.+?(?=\\))\\)".toRegex()
val redundantSymbolsRegex = "_".toRegex()
val singlePairTypeRegex = "(л[/.]р\\.?|пр\\.|лк\\.?)".toRegex(RegexOption.IGNORE_CASE) // At first they took away teacher rank dots. Then this. What's next?
val multiplePairTypesRegex = "$singlePairTypeRegex(,\\s?)?".toRegex(RegexOption.IGNORE_CASE)
val endsWithPracticeRegex = ".*п.?р.?\\.?\$".toRegex() // For the tear down stage.
val unwantedRoomSymbolsRegex = "[^\\dа,\\s-]+".toRegex()
val teacherRankNoDots = "(Доц|Проф|Асс|Асп|[Сс]тпр|[Пп]реп)".toRegex(RegexOption.IGNORE_CASE)
val teacherRankNormalCase = "(Доц\\.?|Проф\\.|Асс\\.?|Асп\\.?|[Сс]т\\.?пр\\.?|[Пп]реп\\.?)".toRegex() // I almost 100% sure i will regret making the dots optional. (I had already, see @testTeacherRankTokenInSubject)
// Do not! "optimize" this with IGNORE_CASE flag.
val teacherRank = "($teacherRankNormalCase|${teacherRankNormalCase.toString().toUpperCase()})".toRegex() // In the future
val teacherInitials = "(\\p{Lu}\\.)(\\p{Lu}\\.*)?".toRegex() // Note that this is only INITIALS, not whole teacher name regex. (includes teacherInitialsNoClosingDot)

/* Special regex */
val whiteSpaceRegex = "[\\040\\0240]".toRegex()

val subGroupRegex = "(гр\\.)?[А-Я]{2,}-\\d{2}.*?(?=а)а".toRegex() // todo consider improving this
/* Check case if beautiful people of the planet Earth who make this schedule
   Accidentally forgot to fill in teacher and just left default "Вакансия" on that field */
val vacancyRegex = "Вакансия(\\s?\\d)?".toRegex()
val teacherRegex = "($teacherRank.*?(?=(\\p{Lu}\\.)(\\p{Lu}\\.?)?)(\\p{Lu}\\.)(\\p{Lu}\\.*)?,*|$vacancyRegex)".toRegex() // Careful! This is for spaceless strings .*?(?=(\p{L}\.)+)(\p{L}\.)+) zzz .*?(?=(\p{L}\.)+)(\p{L}\.)+,*
// val teacherRegex = "($teacherRank\\p{L}+\\.\\p{L}\\.*,*|$vacancyRegex)".toRegex()
val roomRegex = "(I-)?(No|№)-?.+?(?=(No|№))".toRegex() // Check for "I-" before first room token to grab it from original string/
val roomSearchingRegex = "(No|№)+".toRegex()
val roomMiningMuseumRegex = "($roomSearchingRegex\\s?)?(Горн.*муз.*|[Зз]ал\\s*$roomSearchingRegex\\s*\\d{2,})".toRegex()
val roomMiningMuseumWithRoomRegex = "(Горн.*муз.*)?[Зз]ал.*\\d{2,}".toRegex()
val weeksRegex = "(I+-?)".toRegex()
val oneHalfRegex = "[1-4]/[1-5]-?".toRegex()
val overWeekRegex = "ч/н".toRegex()
val lineBreaksRegex = "(\\r\\n|\\n)".toRegex()
val dayRangeRegex = "\\(?$whiteSpaceRegex?с?$whiteSpaceRegex?\\d{1,2}\\.\\d{1,2}($whiteSpaceRegex?(-|по|и)$whiteSpaceRegex?)?(\\d{1,2}\\.\\d{1,2})?$whiteSpaceRegex?\\)?".toRegex() // That's hell of a very slippery regex. Might cause some errors!
val timeSpanRegex = "\\d+\\.\\d{2}-\\d+\\.\\d{2}".toRegex()

/* Converter resources */
val groupRowRegex = "День\\s?недели".toRegex()

/* CSV extractor resources */
val meaningfulTokensRegex = "($weeksRegex|$singlePairTypeRegex|$oneHalfRegex|$overWeekRegex)".toRegex()
val pairNoTeacherRegex = ".*$meaningfulTokensRegex*.*$roomSearchingRegex.*$meaningfulTokensRegex*.*".toRegex()
val pairNoRoomRegex = "$weeksRegex*.*$teacherRegex.*$singlePairTypeRegex*.*".toRegex()
val pairRegex = "$pairNoRoomRegex.*$roomSearchingRegex".toRegex()
val groupRegex = "\\p{Lu}{2,}-\\d{2,}.*".toRegex()

/* Pair splitter resources */
val improvedSubGroupRegex = "г.?р.?\\..?[А-Я].?[А-Я].+?(?=а)а".toRegex()
val oneHalfTokenRegex = ".*[12].?/.?[23].*?(?=((I+|ч.?/.?н|$improvedSubGroupRegex)*.[12].?/.?[23]))".toRegex()
val weekTokenRegex = ".*?(?>I+|ч.?/.?н).*?(?=([12].?/.?[23]|ч.?/.?н|$improvedSubGroupRegex)*.?(I+))".toRegex() // .*((I.*)+|ч.*/.*н).*?(?=(1/\d)*.((I.*)+|ч.*/.*н)) ".*((I).+|ч.?/.?н).*?(?=((гр\\..?[А-Я]{2,}.*?(?=а)а)?.?[12].?/.?[23])*.((I)+|ч.?/.?н))"
val teacherNoRankRegex = "([А-ЯЁ][а-яё]{2,20}\\s?$teacherInitials|$vacancyRegex)".toRegex()

/* Reborn splitter resources */
val roomNumberRegex = "\\d{3,}(а|-\\d)?".toRegex()
val fakeWeekTokenRegex = "\\(\\s?I+".toRegex()
val leadingTokenRegex = "($weeksRegex(?!I?\\s?(-|$roomSearchingRegex))|$oneHalfRegex|$overWeekRegex)".toRegex()
val trailingTokenRegex = "($roomNumberRegex)".toRegex()


/* Raw extractor resources */
val multiweekRoomRegex = "I+.*?-.*?(No|№).*?\\d{2,}.*?I+.*?-.*?(No|№).*?\\d{2,}".toRegex()
val multiplePairRegexVanilla = "I+.+I+.+".toRegex()
val roomNumberTokenRegex = "(No|№)".toRegex()
val multiplePairRegexOneHalf = "1/[23].+1/[23].+".toRegex()
val ripOneLineRegex = "I.+?(?=I\\s*I)".toRegex()
val ripOneHalfRegex = "1/[23].+? (?=1/[23])".toRegex()
val multiplePairRegexOneLine = "I.*?-?.*?(No|№)?\\d{2,}.*?I\\s*I.*?-?.*?((No|№)?\\d{2,}(а|-\\d)?(,.?)?)+".toRegex() // This regex might be a weak spot. TODO optimise it.
