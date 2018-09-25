package com.raritasolutions.mymining.extractor

class SimpleCellExtractor(contents: String,
                          group: String = "ААА-00",
                          timeStarts : String = "00:00",
                          day : Int = 0) : ContentSafeExtractor(contents, group, timeStarts, day)
{

    override val extractRoom: () -> String = {"Спортзал"}
    override val extractWeek: () -> Int = {0}
    override val extractType: () -> String = {"занятие"}
    override val extractTeacher: () -> List<String> = { listOf("NO_TEACHER")}
    override var extractOneHalf: () -> Boolean = {false}

}