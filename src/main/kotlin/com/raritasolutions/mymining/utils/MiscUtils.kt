package com.raritasolutions.mymining.utils

import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties


fun Any.listOfProperties(): List<String> =
        this::class.declaredMemberProperties.map { it.name }

fun Any.toPropertyMap() : Map<String, String>{
    val result = mutableMapOf<String, String>()
    for (prop in this::class.declaredMemberProperties as Collection<KProperty1<Any,*>>)
        result[prop.name] = prop.get(this).toString()
    return result
}

fun Any.findField(name: String)
    = this::class.declaredMemberProperties.find { it.name == name }