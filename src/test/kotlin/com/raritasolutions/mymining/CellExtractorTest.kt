package com.raritasolutions.mymining


import com.raritasolutions.mymining.extractor.cell.ComplexCellExtractor
import com.raritasolutions.mymining.extractor.cell.SimpleCellExtractor
import com.raritasolutions.mymining.model.PairRecord
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.lang.Exception
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
            assert(week == 2)
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
            assert(room == "838, 228(немецкийязык)")
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
}