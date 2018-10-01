package com.raritasolutions.mymining.utils

import com.raritasolutions.mymining.extractor.ContentHolder
// todo maybe rewrite this as extension functions
fun extractCustomRegex(regex : Regex, recipient: ContentHolder) : String?
{
    val item = regex
            .find(recipient._contents)
            ?.value
    item?.let { _item ->  recipient._contents = recipient._contents.replace(_item,"")  }
    return item
}

fun extractCustomRegexToList(regex: Regex, recipient: ContentHolder): List<String>
{
    val items = regex
            .findAll(recipient._contents)
            .map { it.value }
            .toList()
    recipient._contents = recipient._contents.replace(regex,"")
    return items
}

fun raiseParsingException(regex: Regex, caller: ContentHolder): Nothing
        = throw Exception("Regex $regex can't be found in ${caller._contents}")