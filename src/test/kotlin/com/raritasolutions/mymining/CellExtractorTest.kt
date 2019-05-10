package com.raritasolutions.mymining


import com.raritasolutions.mymining.extractor.cell.CellExtractorFactory
import com.raritasolutions.mymining.extractor.cell.implementations.ComplexCellExtractor
import com.raritasolutions.mymining.extractor.cell.implementations.SimpleCellExtractor
import com.raritasolutions.mymining.model.NO_TEACHER
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.isCorrect
import com.raritasolutions.mymining.utils.unwantedRoomSymbolsRegex
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

// todo move inputs to different place (Factory tests use them too)
class CellExtractorTest
{
    @Rule
    @JvmField
    var expectedEx = ExpectedException.none()!!

    @Test
    fun testRegular()
    {
        val input = "Теория принятия решений\n" + "Проф. Иванова И.В. No3424"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with(extractor.result)
        {
            assert(subject == "Теория принятия решений")
            assert(type == "лекция")
            assert(room == "3424")
            assert(teacher == "Проф. Иванова И.В.")
        }
    }

    @Test
    fun testRegularWithUTFRoomToken()
    {
        val input = "Теория принятия решений\n" + "Проф. Иванова И.В. №3424"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with(extractor.result)
        {
            assert(subject == "Теория принятия решений")
            assert(type == "лекция")
            assert(room == "3424")
            assert(teacher == "Проф. Иванова И.В.")
        }
    }

    @Test
    fun testFullStuffed()
    {
        val input = "II 1/2 Маркшейдерские работы\n" +
                "при открытой разработке месторождений\n" +
                "Доц. Голованов В.А. пр. No3411"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with(extractor.result)
        {
            assert(subject == "Маркшейдерские работы при открытой разработке месторождений")
            assert(type == "практика")
            assert(teacher == "Доц. Голованов В.А.")
            assert(room == "3411")
            assert(week == 2)
        }

    }
    @Test
    fun testTooMuchSpaces()
    {
        expectedEx.expect(Exception::class.java)
        expectedEx.expectMessage("Pair is not extracted yet")
        val input = "I     Э  л   е  к т  р  о  т  е  х  н  и  к  а\n" +
                "Доц. Яковлева Э.В. пр. No7213\n"
        val extractor = ComplexCellExtractor(input).apply { make() }
        // this call should result in a throw
        extractor.result
    }

    @Test
    fun testSubjectQueueAid()
    {
        val input = "I     Э  л   е  к т  р  о  т  е  х  н  и  к  а\n" +
                "Доц. Яковлева Э.В. пр. No7213\n"
        val extractor = ComplexCellExtractor(input).apply { make() }
        // Parsing same pair, but with regular amount of spaces
        val inputButCorrect = "I Электротехника\n" +
                "Доц. Яковлева Э.В. пр. No7213\n"
        // Forcing SubjectQueue to add subject to vault using another extractor.
        ComplexCellExtractor(inputButCorrect).apply { make() }
        with (extractor.result)
        {
            assert(subject == "Электротехника")
            assert(week == 1)
            assert(one_half == "")
            assert(teacher == "Доц. Яковлева Э.В.")
            assert(type == "практика")
            assert(room == "7213")
        }
    }

    @Test
    fun testMultipleTeachers()
    {
        val input = "Маркшейдерские и геодезические приборы Доц. Голованов В.А. Доц. Новоженин С.Ю. л/р No3403а\n"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result)
        {
            assert(type == "лабораторная работа")
            assert(room == "3403а")
            assert(teacher == "Доц. Голованов В.А., Доц. Новоженин С.Ю.")
            assert(subject == "Маркшейдерские и геодезические приборы")
        }
    }
    @Test
    fun testMultipleRooms()
    {
        val input = "ч/н 1/2 Финансовый менеджмент\n" +
                "и финансовый анализ\n" +
                "Доц. Любек Ю.В. л/р No4611,4614"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result)
        {
            assert(type == "лабораторная работа")
            assert(room == "4611, 4614")
            assert(over_week == true)
            assert(teacher == "Доц. Любек Ю.В.")
            assert(subject == "Финансовый менеджмент и финансовый анализ")
        }
    }

    @Test
    fun testMultipleTeachersAndRooms()
    {
        val input = "Иностранный язык " +
                "Доц. Герасимова И.Г. пр. No838 " +
                "Доц. Гончарова М.В. пр. No228 " +
                "(немецкий язык)"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result)
        {
            assert(type == "практика")
            assert(room == "228(немецкий язык), 838")
            assert(week == 0)
            assert(teacher == "Доц. Герасимова И.Г., Доц. Гончарова М.В.")
            assert(subject == "Иностранный язык")
        }
    }

    @Test
    fun testUltimate()
    {
        val input = "I 1/2 Финансовый менеджмент\n" +
                "и финансовый анализ\n" +
                "Доц. Любек Ю.В. Проф. Папанин Л.Ю. л/р No4611,4614"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result)
        {
            assert(type == "лабораторная работа")
            assert(room == "4611, 4614")
            assert(week == 1)
            assert(teacher == "Доц. Любек Ю.В., Проф. Папанин Л.Ю.")
            assert(subject == "Финансовый менеджмент и финансовый анализ")
        }
    }

    @Test
    fun testPhysEducation()
    {
        val input = "Физическая культура"
        val extractor = SimpleCellExtractor(input).apply { make() }
        with (extractor.result){
            assert(subject == "Физическая культура")
        }
    }

    @Test
    fun testThreePairs()
    {
        val input = "1/2 Физика\n" +
                "Доц.Томаев В.В.\n" +
                "л/р No235,236,717"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result){
            assert(subject == "Физика")
            assert(teacher == "Доц. Томаев В.В.")
            assert(type == "лабораторная работа")
            assert(room == "235, 236, 717")
        }
    }

    @Test
    fun testNoDotAfterPatronymic(){
        val input = "Геология\n" +
                "Доц. Тутакова А.Я No832"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result){
            assert(subject == "Геология")
            assert(teacher == "Доц. Тутакова А.Я.")
            assert(type == "лекция")
            assert(room == "832")
        }
    }

    @Test
    fun testMultipleRoomTypesInSingleClass() {
        val simpleInput = "Иностранный язык Доц. Зибров Д.А. пр. No626 Доц. Облова И.С. I-No621, II- No716"
        // Needs overridden extractWeek field in production case so consider its week to be always 0.
        var extractor = ComplexCellExtractor(simpleInput).apply { make() }
        with (extractor.result) {
            assert(subject == "Иностранный язык")
            assert(room == "621, 626, 716")
            assert(isCorrect())
        }
        val insaneInput = "Физика Асс. Страхова А.А. л/р No233,235 Доц. Стоянова Т.В. л/р I - No231,236  II -No236,712"
        extractor = ComplexCellExtractor(insaneInput).apply { make() }
        with (extractor.result) {
            assert(subject == "Физика")
            assert("Доц. Стоянова Т.В." in teacher && "Асс. Страхова А.А." in teacher)
            assert(room == "231, 233, 235, 236, 712")
            assert(isCorrect())
        }
        val reversedInput = "Компьютерная графика Доц. Судариков А.Е. л/р I - No729 II - No721 Доц. Исаев А.И. л/р No726"
        extractor = ComplexCellExtractor(reversedInput).apply { make() }
        with (extractor.result) {
            assert(subject == "Компьютерная графика")
            assert("Доц. Судариков А.Е." in teacher && "Доц. Исаев А.И." in teacher)
            assert(room == "721, 726, 729")
            assert(isCorrect())
        }
    }

    @Test
    fun testSubGroupPairExtraction() {
        val input = "гр. НГС-18-1а II Начертательная  геометрия и инженерная графика Доц. Левашов Д.С. No724"
        val inputExcessiveSpaces = "гр. НГС-18-1а II Н а ч е р т а т е л ь н а я  г е о м е т р и я  и инженерная графика Доц. Левашов Д.С. No724"
        var extractor = ComplexCellExtractor(input).apply { make() }
        val result = extractor.result
        with (result) {
            assert(subject == "Начертательная геометрия и инженерная графика")
            assert(teacher == "Доц. Левашов Д.С.")
            assert(room == "724")
            assert(group == "НГС-18-1а")
            assert(isCorrect())
        }
        // Check both inputs just in case.
        extractor = ComplexCellExtractor(inputExcessiveSpaces).apply { make() }
        assert(extractor.result == result)
    }

    @Test
    fun testSubjectWithDashes() {
        val input = "Объектно-ориентированное программирование Доц. Шумова Е.О. л/р No533"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result) {
            assert(subject == "Объектно-ориентированное программирование")
            assert(isCorrect())
        }
    }

    @Test
    fun testPhysEdWithTeacher() {
        val input = "Физическая культура Доц. Изотов И.А."
        val extractor = CellExtractorFactory(input).produce().apply { make() }
        with (extractor.result) {
            assert(subject == "Физическая культура")
            assert(teacher == "Доц. Изотов И.А.")
            assert(isCorrect())
        }
    }

    @Test
    fun testIncorrectPracticeToken() {
        val input = "II Самый важный предмет Доц. Яковлева Ю.А. пр No715"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result) {
            assert(subject == "Самый важный предмет")
            assert(type == "практика")
            assert(isCorrect())
        }
    }

    @Test
    fun testVacancyInTeacherField() {
        val input = "Иностранный язык Вакансия"
        val extractor = CellExtractorFactory(input).produce().apply { make() }
        with (extractor.result) {
            assert(subject == "Иностранный язык")
            assert(teacher == NO_TEACHER)
        }
    }

    @Test
    fun testSubjectStringExtraction() {
        val input = "I Введение в направление Доц. Спиридонов В.В. л/р No315"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result) {
            assert(subject == "Введение в направление")
            assert(type == "лабораторная работа")
            assert(isCorrect())
        }
    }

    @Test
    fun testNoTeacherButHasRoomClass() {
        val input = "I Иностранный язык пр. No636"
        val extractor = CellExtractorFactory(input).produce().apply { make() }
        with (extractor.result) {
            assert(subject == "Иностранный язык")
            assert(type == "практика")
            assert(week == 1)
            assert(room == "636")
            assert(isCorrect())
        }
    }

    @Test
    fun testPEAndFLCorrectType() {
        val inputs = listOf("I Иностранный язык No836", "Физическая культура Доц. Изотов И.А.")
        for (input in inputs){
            val extractor = CellExtractorFactory(input).produce().apply { make() }
            assert(extractor.result.type == "занятие")
        }
    }

    @Test
    fun testShortenedSubject() {
        val input = "1/2 Обогащение полезных ископ.\nДоц. Николаева Н.В. л/р\n№3121,3123,3125"
        val extractor = CellExtractorFactory(input).produce().apply { make() }
        assert(extractor.result.isCorrect())
    }

    @Test
    fun testSpecialSymbolsInRooms() {
        val input = "1/2 Обогащение полезных ископ. Доц. Николаева Н.В. л/р №3121-1,3123а"
        val extractor = CellExtractorFactory(input).produce().apply { make() }
        with (extractor.result) {
            assert(isCorrect())
            assert(room == "3121-1, 3123а")
        }
    }

    @Test
    fun testShortSubgroup() {
        val input = "ТОА-16а\n" +
                "II Экономика и управление\n" +
                "машиностроительным производством Асс. Головина Е.И. пр. №4509"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result) {
            assert(group == "ТОА-16а")
            assert(isCorrect())
        }
    }

    @Test
    fun testAbbreviatedSubjectExtraction() {
        val input = "II ГМ и оборудование Доц. Лавренко С.А. пр. №5406"
        val extractor = ComplexCellExtractor(input).apply { make() }
        with (extractor.result) {
            assert(subject == "ГМ и оборудование")
            assert(isCorrect())
        }
    }

    @Test
    fun testSameSubjectAndTeacherGreed() {
        val input = "I Петрография Доц. Петров Д.А. л/р №4307  Проф. Сироткин А.Н. л/р №4303\n"
        val extractor = ComplexCellExtractor(input).apply { make() }
        assert(extractor.result.isCorrect())
    }

    @Test
    fun testTeacherNoRankCustomExtractor() {
        val input = "I 1/2 Лабор. методы изучения " +
                "минералов, пород и руд, ч. 2 " +
                "Доц. Симаков А.П. л/р №4315 " +
                ".Проф. Гульбин Ю.Л. л/р №4313 " +
                "Проф. Войтеховский Ю.Л. №4309 " +
                " Ларгузова А.В. л/р №3313 " +
                "Асс. Кургузова А.В. л/р №3313 " +
                "Доц. Васильев Е.А. л/р №3312 " +
                "Асс. Гембицкая И.М. л/р №3310"
        val extractor = CellExtractorFactory(input).produce().apply { make() }
        with (extractor.result) {
            assert(isCorrect())
            assert("4309, 4313, 4315" in room) // Test correct spacing
            assert(!room.contains(unwantedRoomSymbolsRegex))
        }
    }

    @Test
    fun testMultiplePairTypes() {
        val input = listOf("Автоматизированное проектирование средств и систем управления Доц. Румянцев В.В. лк.,пр. №3502",
                "Методы принятия инженерных решений Доц. Гусаров И.Е. лк, пр. №1320")
        val extractor = input.map { CellExtractorFactory(it).produce().apply { make() }.result }
        with (extractor) {
            assert(all { it.type == "занятие" } )
            assert(all(PairRecord::isCorrect))
        }
    }

    @Test
    fun testTeacherVacancyWithSpecifiedRoom() {
        val input = "Иностранный язык Доц. Филясова Ю.А. пр. №905 Вакансия пр. №623"
        val extractor = CellExtractorFactory(input).produce().apply { make() }
        with (extractor.result) {
            assert(isCorrect())
            assert(room == "623, 905")
            assert(teacher == "Доц. Филясова Ю.А., Нет Преподавателя")
        }
    }

    @Test
    fun testTeacherRankWithNoTrailingDot() {
        val input = "Иностранный язык Преп. Никифоровская Е.О. пр. №903 Доц Горохова Н.Э. пр. №803"
        val extractor = CellExtractorFactory(input).produce().apply { make() }
        with (extractor.result) {
            assert(isCorrect())
            assert(teacher == "Преп. Никифоровская Е.О., Доц. Горохова Н.Э.")
        }
    }

    @Test
    fun testRoomInMiningMuseumExtraction() {
        val input = "ч/н 1/2 Ювелирные, поделочные и облицовочные камни Доц. Боровкова Н.В. л/р Горн. музей зал №16"
        val extractor = CellExtractorFactory(input).produce().apply { make() }
        with (extractor.result) {
            assert(isCorrect())
            assert(subject == "Ювелирные, поделочные и облицовочные камни")
            assert(room == "Горный музей, Зал 16")
        }
    }

}