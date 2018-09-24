package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.FullCellExtractor
import com.raritasolutions.mymining.extractor.SimpleCellExtractor
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.lang.Exception

class FullCellExtractorTest
{
    @Rule
    @JvmField
    var expectedEx = ExpectedException.none()!!

    @Test
    fun testRegular()
    {
        val input = "Теория принятия решений\n" + "Проф. Иванова И.В. No3424"
        val extractor = FullCellExtractor(input).apply { make() }
        with(extractor.result)
        {
            assert(subject == "Теория принятия решений")
            assert(type == "лекция")
            assert(room == 3424)
            assert(teacher == "Проф. Иванова И.В.")
        }

    }
    @Test
    fun testFullStuffed()
    {
        val input = "II 1/2 Маркшейдерские работы\n" +
                "при открытой разработке месторождений\n" +
                "Доц. Голованов В.А. пр. No3411"
        val extractor = FullCellExtractor(input).apply { make() }
        with(extractor.result)
        {
            assert(subject == "Маркшейдерские работы при открытой разработке месторождений")
            assert(type == "практика")
            assert(teacher == "Доц. Голованов В.А.")
            assert(room == 3411)
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
        val extractor = FullCellExtractor(input).apply { make() }
        // this call should result in a throw
        extractor.result
    }

    @Test
    fun testMultipleTeachers()
    {
        val input = "Маркшейдерские и геодезические приборы Доц. Голованов В.А. Доц. Новоженин С.Ю. л/р No3403\n"
        val extractor = FullCellExtractor(input).apply { make() }
        with (extractor.result)
        {
            assert(type == "лабораторная работа")
            assert(room == 3403)
            assert(teacher == "Доц. Голованов В.А.")
            assert(subject == "Маркшейдерские и геодезические приборы")
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
}