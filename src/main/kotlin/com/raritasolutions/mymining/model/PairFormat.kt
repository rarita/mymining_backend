package com.raritasolutions.mymining.model



interface BasePairFormat{
    fun PairRecord.stringRepr(): String
}

val roomPairFormat = object : BasePairFormat {
    override fun PairRecord.stringRepr() = if (this.one_half) "1/2 " else "" +
            (if (week > 0) "I".repeat(week) + " " else "") +
            this.subject + "\n" + this.teacher + "\n" +
            this.group
}