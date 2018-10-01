package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

/* Extending this extractor guarantees that you will extract a pair with proper name */
abstract class ContentSafeExtractor(private val contents: String,
                                    group: String = "ААА-00",
                                    timeStarts : String = "00:00",
                                    day : Int = 0) : BaseExtractor {

    // Toggles when correct pair name is found
    private var extractionFinished = false

    // Backing field for result
    private val pairInstance = PairRecord(group = group,
            t_start = timeStarts,
            day = day,
            duration = 90)

    override val result: PairRecord
        get() = if (extractionFinished) pairInstance else throw Exception("Pair is not extracted yet")

    override var _contents: String = contents.removeSpecialCharacters()


    override fun make()
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
            SubjectQueue.subscribe(_subject, this@ContentSafeExtractor)
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
        val regex = "${subject.take(3)}.+${subject.takeLast(3)}".toRegex()
        // Replace line breaks with spaces before searching
        val contentsNoLineBreaks = contents.replace(lineBreaksRegex," ")
        return regex
                .find(contentsNoLineBreaks)?.value
                ?: throw Exception("Original subject can't be extracted. Subject is $subject and contents is $contents")
    }

}