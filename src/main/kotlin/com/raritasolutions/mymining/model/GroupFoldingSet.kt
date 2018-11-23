package com.raritasolutions.mymining.model


class GroupFoldingSet(private val contents: Set<PairRecord>) : Set<PairRecord> {
    constructor(vararg elements: PairRecord) : this(setOf(*elements))

    private val container: Set<PairRecord> by lazy {
            contents.fold(setOf<PairRecord>()) { acc, item ->
                val parent = acc.find { it.equalsExcluding(item,PairRecord::group) }
                if (parent != null) {
                    parent.group += ", ${item.group}"
                    return@fold acc
                }
                acc + item
            }
        }

    override val size: Int
        get() = container.size

    override fun contains(element: PairRecord): Boolean
        = container.find { it == element } != null

    override fun containsAll(elements: Collection<PairRecord>): Boolean
        = elements.all { container.contains(it) }

    override fun isEmpty(): Boolean
        = container.isEmpty()

    override fun iterator(): Iterator<PairRecord>
        = container.iterator()

    override fun toString()
        = container.toString()

    operator fun plus(other: PairRecord): GroupFoldingSet
        = GroupFoldingSet(container + other)

    operator fun plus(other: Collection<PairRecord>): GroupFoldingSet
        = GroupFoldingSet(container + other)

}