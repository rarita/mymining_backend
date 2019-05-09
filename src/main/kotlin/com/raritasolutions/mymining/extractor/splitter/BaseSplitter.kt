package com.raritasolutions.mymining.extractor.splitter

interface BaseSplitter {
    val initialContents: String
    val separatedContents: List<String>
}