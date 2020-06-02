package com.raritasolutions.mymining.service.ruz

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.EMPTY
import com.raritasolutions.mymining.utils.preLast
import org.slf4j.LoggerFactory

class RUZPairRecordDeserializer : StdDeserializer<PairRecord>(PairRecord::class.java) {

    private val logger = LoggerFactory.getLogger(RUZPairRecordDeserializer::class.java)

    private fun TreeNode.string()
        = (this as TextNode).textValue()

    private fun TreeNode.formatTime()
        = this.string()
            .replace(':', '.')

    private fun TreeNode.toBuildingID()
        = with (this.string()) {
        if (this.last().isDigit())
            Character.getNumericValue(this.last())
        else
            3
    }

    private fun TreeNode.toPairType(): String {
        return with (this.string()) {
            when {
                this.contains(',') -> {
                    logger.info("Pair type $this has an ',' in it!")
                    this.toLowerCase()
                }
                this.startsWith("лаб") && !this.contains(',') -> "лабораторная работа"
                else -> this.toLowerCase()
            }
        }
    }

    private fun TreeNode.toOneHalf()
        = if (!(this as ValueNode).isNull) "1/${this.string().preLast()}" else String.EMPTY

    private fun TreeNode.toAuditorium()
        = with(this.string()) {
      if (!this.contains("горн."))
          this.replace('.', ',')
        else
          this
    }

    private fun TreeNode.findGroup()
        = if (!(this["group"] as ValueNode).isNull)
            this["group"].string()
        else
            this["subGroup"].string().substringBefore(" (")

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): PairRecord {
        val pairRecord = PairRecord()
        val jsonTree = p?.codec?.readTree<TreeNode>(p)
                ?: throw IllegalStateException("JsonParser supplied to deserialize() is null")

        pairRecord.room = jsonTree["auditorium"].toAuditorium()
        pairRecord.timeSpan = "${jsonTree["beginLesson"].formatTime()}-${jsonTree["endLesson"].formatTime()}"
        pairRecord.buildingID = jsonTree["building"].toBuildingID()
        pairRecord.day = (jsonTree["dayOfWeek"] as IntNode).intValue()
        pairRecord.subject = jsonTree["discipline"].string()
        pairRecord.group = jsonTree.findGroup()
        pairRecord.type = jsonTree["kindOfWork"].toPairType()
        pairRecord.teacher = jsonTree["lecturer"].string()
        pairRecord.one_half = jsonTree["subGroup"].toOneHalf()

        return pairRecord
    }

}