package com.raritasolutions.mymining.model.viewmodel

data class PairViewModel(val timeStart: String,
                         val timeEnd: String,
                         val tokens: List<String>,
                         val subject: String,
                         val teacher: String,
                         val rooms: List<String>) : BaseViewModel

