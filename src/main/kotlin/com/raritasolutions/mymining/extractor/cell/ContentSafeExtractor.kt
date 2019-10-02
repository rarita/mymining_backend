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
        get() = if (extractionFinished) pairInstance else throw Exception("Pair is not extracted yet: $_contents")

    override var _contents: String = contents
            .removeSpecialCharacters()
            .removeRedundantCharacters()


    override fun make()
    {
        // Applying extraction techniques to pairInstance and receiving spaceless (and bracketless) subject
        val _subject = pairInstance
                .extract()
                .removeContentInBraces()

        check(_subject.isNotBlank()) { "Subject appears to be blank for contents = $contents" }
        /* If the space amount in the string is OK finish extracting and register subject string
        found in contents to global DB. If not, wait until correct subject string is found and
        then finish the extraction  */
        if (hasValidSpaceAmount()) {
            // Also make sure that the smallest original subject is always selected.
            val localSubject = getOriginalSubjectName(_subject)
            val vaultSubject = SubjectQueue.getCorrectSubjectFor(_subject)

            if (localSubject.length < vaultSubject?.length ?: Int.MAX_VALUE) {
                pairInstance.subject = localSubject
                SubjectQueue.addNewRecord(pairInstance.subject)
            }
            else
                pairInstance.subject = vaultSubject!!
            extractionFinished = true
        }
        else
            SubjectQueue.subscribe(_subject, this@ContentSafeExtractor)
    }

    // Checks if ORIGINAL string has an adequate amount of spaces (not more than 22.5% of the whole input)
    private fun hasValidSpaceAmount() : Boolean
    {
        val spaceCount = contents
                .count {(it == ' ') or (it == '\n')}
        return spaceCount.toDouble() / contents.length <= 0.225
    }

    // This gets called when SubjectQueue finds adequate subject for this Extractor
    fun assignCorrectSubject(subject: String)
    {
        pairInstance.subject = subject
        extractionFinished = true
    }

    // Look for minimal distinctive start-end patterns to extract subject string from original contents.
    // Note that it ditches braces content completely. Hope to get rid of this in the future.
    private fun findMinimalWorkingGreed(subject: String): Int {
        val contentsFixed = contents
                .replace(lineBreaksRegex," ")
                .removeContentInBraces()
        var greed = 1 // It is pointless to start @ 1 (0) b.c 90% of the time it is going to be skipped
        var endOccurrences : Int
        do {
            greed++
            // Start occurrences doesn't really matter since there is nothing in string
            // Before the subject itself that can be treated as the part of the subject
            endOccurrences = contentsFixed.countRegex(subject.takeLast(greed).mayContainSpaces().toRegex())
            check(greed <= subject.length / 2) { "Greed is longer than the whole subject for contents = $contents" }
        } while (endOccurrences != 1)
        return greed
    }

    // Count of symbols drawn now depends on subject length.
    private fun getOriginalSubjectName(subject: String): String
    {
        val greed = findMinimalWorkingGreed(subject)
        // Should match first letter and last letter of Subject
        val prefix = subject.take(greed)
                                    .shieldSymbol('(')
                                    .mayContainSpaces()
        val postfix = subject.takeLast(greed)
                                    .shieldSymbol(')')
                                    .mayContainSpaces()

        val regex = "$prefix.*$postfix".toRegex() // NOTE it was made NOT GREEDY for data between keys
        // Replace line breaks with spaces before searching (and the braces and content in them)
        val contentsNoLineBreaks = contents
                .replace(lineBreaksRegex," ")
                .removeContentInBraces()
        return regex.find(contentsNoLineBreaks)
                ?.value
                ?.trim()
                ?.replace("$whiteSpaceRegex+".toRegex(), " ") // Replace duplicating whitespaces if present
                ?: throw Exception("Original subject can't be extracted. Subject is $subject and contents is $contents " +
                        "@ [${this.pairInstance.group},${this.pairInstance.day},${this.pairInstance.timeSpan}]")
    }

}