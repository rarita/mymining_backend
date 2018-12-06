package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

/* Extending this extractor guarantees that you will extract a pair with proper name */
abstract class ContentSafeExtractor(private val contents: String,
                                    basePair: PairRecord) : BaseExtractor {

    // Toggles when correct pair name is found
    private var extractionFinished = false
    // Breaking the link with parameter so one pair belongs only to one extractor.
    private val pairInstance = basePair.copy()

    override val result: PairRecord
        get() = if (extractionFinished) pairInstance else throw Exception("Pair is not extracted yet: $_contents,")

    override var _contents: String = contents
            .removeSpecialCharacters()
            .removeRedundantCharacters() // TODO BIND DASH-INCLUDING PAIRS INTO SubjectQueue


    override fun make()
    {
        // Applying extraction techniques to pairInstance and receiving spaceless (and bracketless) subject
        val _subject = pairInstance
                .extract()
                .removeContentInBraces()
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
    /* NOTE: never take less or more than 2 symbols:
    - it often grabs wrong bracket if you take one
    - it fails on "Химия" when you grab 3
    2 is perfect
     */
    private fun getOriginalSubjectName(subject: String): String
    {
        val greed = 2
        // Should match first letter and last letter of Subject
        val prefix = subject.take(greed)
                                    .shieldSymbol('(')
                                    .mayContainSpaces()
        val postfix = subject.takeLast(greed)
                                    .shieldSymbol(')')
                                    .mayContainSpaces()

        val regex = "$prefix.*?$postfix".toRegex() // NOTE it was made NOT GREEDY for data between keys
        // Replace line breaks with spaces before searching
        val contentsNoLineBreaks = contents.replace(lineBreaksRegex," ")
        return regex
                .find(contentsNoLineBreaks)?.value
                ?.trim()
                ?.replace("\\s+".toRegex(), " ") // Replace duplicating whitespaces if present
                ?: throw Exception("Original subject can't be extracted. Subject is $subject and contents is $contents " +
                        "@ [${this.pairInstance.group},${this.pairInstance.day},${this.pairInstance.timeSpan}]")
    }

}