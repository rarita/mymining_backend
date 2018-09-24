package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

/* Extending this extractor guarantees that you will extract a pair with proper name */
abstract class ContentSafeExtractor(protected val contents: String,
                                    group: String = "ААА-00",
                                    timeStarts : String = "00:00",
                                    day : Int = 0) {

    private var extractionFinished = false

    private val pairInstance = PairRecord(group = group,
            t_start = timeStarts,
            day = day,
            duration = 90)

    val result: PairRecord
        get() = if (extractionFinished) pairInstance else throw Exception("Pair is not extracted yet")


    // I feel like this solution is not equal to good design practices
    // This must return subject string without spaces
    internal abstract fun PairRecord.extract(): String

    fun make()
    {
        // Applying extraction techniques to pairInstance and receiving spaceless subject
        val _subject = pairInstance.extract()
        /* If the space amount in the string is OK finish extracting and register subject string
        found in contents to global DB. If not, wait until correct subject string is found and
        then finish the extraction  */
        if (hasValidSpaceAmount()) {
            pairInstance.subject = getOriginalSubjectName(_subject)
            extractionFinished = true
            SubjectQueue.addNewRecord(pairInstance.subject)
        }
        else
            SubjectQueue.subscribe(_subject,this@ContentSafeExtractor)
    }

    // Checks if ORIGINAL string has an adequate amount of spaces (not more than 20% of the whole input)
    private fun hasValidSpaceAmount() : Boolean
    {
        val spaceCount = contents.count {(it == ' ') or (it == '\n')}
        return spaceCount.toDouble() / contents.length <= 0.2
    }
    // This gets called when SubjectQueue finds adequate subject for this Extractor
    fun assignCorrectSubject(subject: String)
    {
        pairInstance.subject = subject
        extractionFinished = true
    }
    private fun getOriginalSubjectName(subject: String): String
    {
        // Should match first letter and last letter of Subject
        val regex = "${subject.take(1)}.+${subject.takeLast(1)}".toRegex()
        // Replace line breaks with spaces before searching
        val contentsNoLineBreaks = contents.replace(lineBreaksRegex," ")
        return regex
                .find(contentsNoLineBreaks)?.value
                ?: throw Exception("Original subject can't be extracted. Subject is $subject and contents is $contents")
    }

}