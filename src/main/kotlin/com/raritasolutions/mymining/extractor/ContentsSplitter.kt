package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.utils.extractCustomRegex
import com.raritasolutions.mymining.utils.raiseParsingException
import java.lang.IllegalStateException

// NOTE: Don't pass multiplePair...Regex here! It's designed for rip...Regex!
// Todo: maybe it's worth to rewrite it to use Pair<String, String> instead of List<String>
class ContentsSplitter(override var _contents: String,
                       private val rippingRegex: Regex) : ContentHolder {

    val result: ArrayList<String>
        get(){
            val _result = arrayListOf<String>()
            _result.add(extractCustomRegex(rippingRegex,this)?.trim()
                    ?: raiseParsingException(rippingRegex,this))
            if (_contents.isBlank()) throw IllegalStateException("After extraction of $rippingRegex contents had emptied")
            _result.add(_contents.trim())
            return _result
        }

}