package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.RawConverter
import com.raritasolutions.mymining.model.*
import org.junit.Test

class RawConverterTest {

    private fun getOutput(rpl : List<RawPairRecord>): List<PairRecord>
    {
        val extractors = RawConverter(rpl, ExtractionReport()).extractorList
        extractors.forEach { it.make() }
        return extractors.map { it.result }
    }

    private fun makeFromString(input: String, formatting: List<BuildingData>? = null)
        = listOf(RawPairRecord(5,"12:35-14:05", "ТЕС-16", input, formatting))

    @Test
    fun testBasicCause() {
        val contents = listOf("I  Культурология Доц. Науменко Н.В. пр. No610 II Минерально-сырьевая база Российской Федерации Проф. Евдокимов А.Н. пр. No528",
                "1/2 Информатика Доц. Пивоварова И.И. л/р No345 1/2 Общая геология Асс. Илалова Р.К. л/р No550",
                "1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840")
        val rpl: List<RawPairRecord> = contents.map { RawPairRecord(3, "10:35-12:05", "АБВ-12-3", it) }
        val results = getOutput(rpl)
        assert(PairRecord(id = 0,subject = "Информатика",teacher = "Доц. Пивоварова И.И.",timeSpan = "10:35-12:05",group = "АБВ-12-3", room = "345", type = "лабораторная работа",day = 3,week = 0,one_half = "1/2") in results)
    }

    @Test
    fun testOneLinedRooms()
    {
        val rpl = listOf(RawPairRecord(4,"08:50-10:20","БАД-16","1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840"))
        val results = getOutput(rpl)
        val expectedOutput = listOf(PairRecord(id=0, group="БАД-16", teacher="Доц. Джевага Н.В.", week=1, day=4, timeSpan="08:50-10:20", subject="Химия элементов и их соединений", room="844", type="лабораторная работа", one_half="1/2"),
                                    PairRecord(id=0, group="БАД-16", teacher="Доц. Джевага Н.В.", week=2, day=4, timeSpan="08:50-10:20", subject="Химия элементов и их соединений", room="840", type="лабораторная работа", one_half="1/2"))
        assert(expectedOutput.intersect(results).size == expectedOutput.size)
    }

    @Test
    fun testMixedCause() {
        val rpl = listOf(RawPairRecord(4, "08:50-10:20", "БАД-16", "1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840 1/2 Информатика Доц. Косарев О.В. л/р No548 "))
        val results = getOutput(rpl)
        val expectedOutput = listOf(PairRecord(id = 0, group = "БАД-16", teacher = "Доц. Джевага Н.В.", week = 1, day = 4, timeSpan = "08:50-10:20", subject = "Химия элементов и их соединений", room = "844", type = "лабораторная работа", one_half = "1/2"),
                PairRecord(id = 0, group = "БАД-16", teacher = "Доц. Джевага Н.В.", week = 2, day = 4, timeSpan = "08:50-10:20", subject = "Химия элементов и их соединений", room = "840", type = "лабораторная работа", one_half = "1/2"),
                PairRecord(id = 0, group = "БАД-16", teacher = "Доц. Косарев О.В.", week = 0, day = 4, timeSpan = "08:50-10:20", subject = "Информатика", room = "548", type = "лабораторная работа", one_half = "1/2"))
        assert(expectedOutput.intersect(results).size == expectedOutput.size)
    }

    @Test
    fun testOverWeekCase(){
        val rpl = makeFromString("ч/н 1/2 Общая и неорганическая химия Доц. Лобачёва О.Л. Доц. Джевага Н.В. л/р I -  No845  II - No842")
        val results = getOutput(rpl)
        results.forEach { assert(it.isCorrect()) }
    }

    @Test
    fun testOverWeekTooMuchSpacesCase() {
        val rpl = makeFromString("ч/н   1/2   О  б   щ   а  я  и неорган. химия Д  о  ц   .  Д  ж   е  в  а  г а   Н   . В  . Д   о  ц  .  Лобачёва О.Л. л/р No843 ч/н    1/2    И   н  ф   о  р  м   а  т и   к а Доц. Ильин А.Е. л/р No336")
        val results = getOutput(rpl)
        with (results[0]) {
            assert(over_week)
            assert(teacher == "Доц. Джевага Н.В., Доц. Лобачёва О.Л.")
            assert(subject == "Общая и неорганическая химия")
        }
        with (results[1]) {
            assert(over_week)
            assert(teacher == "Доц. Ильин А.Е.")
            assert(subject == "Информатика")
        }
    }

    @Test
    fun testRegexProofCase() {
        // this case happened because of regex being too greedy for overWeek tokens
        val rpl = makeFromString("I Химия Доц. Лобачёва О.Л. пр. No813 II Химия  Доц. Лобачёва О.Л. л/р No843 Асс. Черняев В.А. л/р No842")
        val results = getOutput(rpl)
        with (results[0]) {
            assert(subject == "Химия")
            assert(teacher == "Доц. Лобачёва О.Л.")
            assert(room == "813")
            assert(isCorrect())
        }
        with (results[1]) {
            assert(subject == "Химия")
            assert(teacher == "Доц. Лобачёва О.Л., Асс. Черняев В.А.")
            assert(room == "842, 843")
            assert(isCorrect())
        }
    }

    @Test
    fun testAbsenceOfRoomNumberToken() {
        val rpl = makeFromString("I Математика Доц. Керейчук М.А. пр. No718 II  Философия Доц. Рахманинова М.Д. пр. 540")
        val results = getOutput(rpl)
        results.forEach { assert(it.isCorrect()) }
        assert(results[1].subject == "Философия")
        assert(results[1].room == "540")
    }

    @Test
    fun testUpperCaseTeacherRank() {
        val rpl = makeFromString("Иностранный язык ДОЦ. Зибров Д.А. пр. No626 Доц. Облова И.С. No716")
        val results = getOutput(rpl)
        results.forEach {
            assert(it.isCorrect())
            assert(it.teacher == "Доц. Зибров Д.А., Доц. Облова И.С.")
        }
    }

    @Test
    fun testAbsentRoomNumberTokenAtMultiline() {
        val rpl = makeFromString("1/2 Информационные технологии в менеджменте Доц. Косовцева Т.Р. л/р I - 551 II - No515")
        val results = getOutput(rpl)
        assert( results.any { it.room == "515" || it.room == "551" } )
        results.forEach { assert(it.isCorrect()) }
    }

    @Test
    fun testTripledCellContents() {
        val rpl = makeFromString("I 1/2 Химия Асс. Куртенков Р.В. л/р No845 II 1/2 Физика Асс. Страхова А.А. No235,236,712 1/2 Информатика Доц. Акимова Е.В. л/р No643")
        val results = getOutput(rpl)
        assert(results.size == 3)
        results.forEach { assert(it.isCorrect()) }
        with (results[0]) { assert(subject == "Химия" && room == "845") }
        with (results[1]) { assert(subject == "Физика" && room == "235, 236, 712") }
        with (results[2]) { assert(subject == "Информатика" && room == "643") }
    }

    @Test
    fun testSpaceAmountReduction() {
        val rpl = makeFromString("Физическая культура                                                ( 5.09 - 19.09 )                                                                                                                                                   Элективные дисциплины по                           физической культуре и спорту                                                                                                ( 19.9 - 26.12 )")
        val rplExtended = rpl + makeFromString("Физическая культура ( 5.09 - 19.09 ) Элективные дисциплины по физической культуре и спорту ( 19.9 - 26.12 )")
        val results = getOutput(rplExtended)
        assert(results[0] == results[1])
    }

    @Test
    fun testSubGroupTokenSplitting() {
        val rpl = makeFromString("I  Теория менеджмента Доц. Никулина А.Ю. пр. No611 гр. МП-18-2а II Теория менеджмента Доц. Никулина А.Ю. No820")
        val result = getOutput(rpl)
        assert(result.size == 2)
        assert(result[1].group == "МП-18-2а")
    }

    @Test
    fun testSingleColoredContents() {
        val singleColoredContents = makeFromString(
                input = "Термодинамика и кинетика Доц. Жадовский И.Т. л/р No3533",
                formatting = listOf(BuildingData(0, 1)))
        val converter = RawConverter(singleColoredContents, ExtractionReport(), 3)
        val result = converter.extractorList.map { it.apply { it.make() } }
        assert(result.size == 1)
        assert(result.first().result.buildingID == 1)
    }

    @Test
    fun testMultipleColoredContents() {
        val multiColorContents = makeFromString(input = "ч/н 1/2 Операционные системы Доц. Спиридонов В.В. л/р No3524 " +
                "ч/н 1/2 Маршрутизация и коммутация компьютерных сетей " +
                "Ст.пр. Жуковский В.Е. пр. No345",
                formatting = listOf(BuildingData(0, 1), BuildingData(60, 3)))
        val converter = RawConverter(multiColorContents, ExtractionReport(), 1)
        val result = converter.extractorList.map { it.apply { it.make() } }
        assert(result.size == 2)
        assert(result.first().result.buildingID == 1)
        assert(result.last().result.buildingID == 3)
    }

    @Test
    fun testHalfCorrectRoomNumberToken() {
        val seriously = makeFromString(
                input = "ч/н 1/2 Технология и безопасность взрывных работ Доц. Хохлов С.В. л/р I - №1205-1 II - 1208")
        val converter = RawConverter(seriously, ExtractionReport(), 3)
        val result = converter.extractorList.map { it.apply { it.make() } }
        assert(result.size == 2)
        assert(result.map { it.result.isCorrect() }.all { it })
    }

    @Test
    fun testMultiweekRoomSpecialTokens() {
        val input = makeFromString("1/2 Материаловедение Доц. Трушко О.В. л/р I - №3104-1  II - №3104а") +
                makeFromString("1/2 Физика Доц. Пушко О.В. л/р I - №3104а  II - №3104-1")
        val result = getOutput(input)
        with (result) {
            assert(all(PairRecord::isCorrect))
            assert(get(0).room == "3104-1" && get(3).room == "3104-1")
            assert(get(1).room == "3104а" && get(2).room == "3104а")
        }
    }

    @Test
    fun testMultiWeekManyRoomsExtraction() {
        val input = makeFromString("1/2 ГИС в экологии и природопользовании Доц. Стриженок А.В. л/р №1307 1/2 Инженерная геология и гидрогеология Доц. Норова Л.П. л/р I - №3205 II - №3203, 3205")
        val result = getOutput(input)
        with (result) {
            assert(all(PairRecord::isCorrect))
            assert(get(0).room == "1307")
            assert(get(1).room == "3205")
            assert(get(2).room == "3203, 3205")
        }
    }

    @Test
    fun testBrokenMultiWeekToken() {
        val input = makeFromString("ч/н 1/2 Гидравлика Асс. Мерзляков М.Ю. л/р I №7206 II -№7301")
        val result = getOutput(input)
        with (result) {
            assert(all(PairRecord::isCorrect))
            assert(size == 2)
            assert(first().room == "7206")
            assert(last().room == "7301")
            assert(first().equalsExcluding(last(), listOf(PairRecord::week, PairRecord::room)))
        }
    }

    @Test
    fun testTeacherRankTokenInSubject() {
        val input = makeFromString("I  Профессиональная архитектурная практика\n" +
                "Доц. Гайкович С.В. пр. №3519\n" +
                "II Профессиональная архитектурная практика Доц. Гайкович С.В. №3519")
        val result = getOutput(input)
        assert(result.size == 2)
        assert(result.all { it.subject == "Профессиональная архитектурная практика" })
        assert(result.all { it.room == "3519" })
    }

    @Test
    fun testIncompleteDayRangeWithRoom() {
        val input = makeFromString("Моделирование физических процессов в горном деле " +
                "Доц. Карасёв М.А. пр. №4401 с 16.04 - №3527")
        val result = getOutput(input)
        assert(result.size == 1)
        with(result.first()) {
            assert(isCorrect())
            assert(subject == "Моделирование физических процессов в горном деле")
            assert(room == "3527, 4401")
        }
    }

    @Test
    fun testTrailingCommasInMultiWeekPairs() {
        val input = makeFromString("Основы научных исследований Доц. Ковальский Е.Р.\n" +
                "I №820 , II №347")
        val result = getOutput(input)
        assert(result.size == 2)
        with(result) {
            assert(all(PairRecord::isCorrect))
            assert(first().room == "820")
            assert(last().room == "347")
        }
    }

    @Test
    fun testDoubleMultiWeekRooms() {
        // In perfect case this should be split to 2 pairs
        val input = makeFromString("Начертательная геометрия Асс. Кононов П.В. пр.\n" +
                "I - №719  II - №724\n" +
                "Доц. Третьякова З.О. пр.\n" +
                "I -  №729  II - №728")
        val result = getOutput(input)
        assert(result.size == 1)
        with(result.first()) {
            assert(room == "719, 724, 728, 729")
            assert(subject == "Начертательная геометрия")
            assert(week == 0)
            assert("Асс. Кононов П.В." in teacher && "Доц. Третьякова З.О." in teacher)
        }
    }

    @Test
    fun testSubjectWithDotsInIt() {
        val input = makeFromString("I  Методы обоснования нормативов и системы технической эксплуатации АТС.\n" +
                " Доц. Кацуба Ю.Н. лк, пр. №3306\n" +
                "II Методы обоснования нормативов и системы технической эксплуатации АТС.\n" +
                "Доц. Кацуба Ю.Н. пр. №3306")
        val result = getOutput(input)
        assert(result.size == 2)
        with (result) {
            assert(all { it.isCorrect() })
            assert(all { it.room == "3306" })
            assert(all { it.subject == "Методы обоснования нормативов и системы технической эксплуатации АТС." })
            assert(all { it.teacher == "Доц. Кацуба Ю.Н." })
        }
    }

    @Test
    fun testTrailingTokenSplitsByNewLineCharacter() {
        val input = makeFromString("Физическая культура Ст.пр. Ларионова М.Н.\n" +
                "Практика по корпоративной отчётности в горных компаниях\n" +
                "Проф. Пономаренко Т.В. пр. №4614 (30.12)")
        val result = getOutput(input)
        assert(result.size == 2)
        with (result) {
            assert(all { it.isCorrect() })

            assert(first().teacher == "Ст.пр. Ларионова М.Н.")
            assert(first().subject == "Физическая культура")

            assert(last().teacher == "Проф. Пономаренко Т.В.")
            assert(last().subject == "Практика по корпоративной отчётности в горных компаниях")
        }
    }

}