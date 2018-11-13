package com.raritasolutions.mymining.utils

import com.raritasolutions.mymining.model.PairRecord
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

fun Any.toPropertyMap() : Map<String, String>{
    val result = mutableMapOf<String, String>()
    for (prop in this::class.declaredMemberProperties as Collection<KProperty1<Any,*>>)
        result[prop.name] = prop.get(this).toString()
    return result
}
