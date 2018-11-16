package com.raritasolutions.mymining.model

import kotlin.reflect.KMutableProperty1

class FoldingSet<T>(private val contents: Set<T>, private val foldingProp: KMutableProperty1<T, String>, private val equals: T.(T) -> Boolean) : Set<T> {
    constructor(vararg elements: T, foldingProp: KMutableProperty1<T, String>, equals: T.(T) -> Boolean) : this(setOf(*elements), foldingProp, equals)

    private val container: Set<T> by lazy {
            contents.fold(setOf<T>()) { acc, item ->
                val parent = acc.find { it.equals(item) }
                if (parent != null) {
                    val prevValue = foldingProp.get(parent)
                    val itemValue = foldingProp.get(item)
                    foldingProp.set(parent, "$prevValue, $itemValue")
                    acc
                }
                acc + item
            }
        }

    override val size: Int
        get() = container.size

    override fun contains(element: T): Boolean
        = container.find { it.equals(element) } != null

    override fun containsAll(elements: Collection<T>): Boolean
        = elements.all { container.contains(it) }

    override fun isEmpty(): Boolean
        = container.isEmpty()

    override fun iterator(): Iterator<T>
        = container.iterator()

    override fun toString()
        = container.toString()
}