package com.raritasolutions.mymining.service.base

/**
 * Interface shows that the implementing class
 * can provide schedule data to the recipient
 *
 * Having the highest level of abstraction, it does not
 * force user to inject a set of objects usually needed
 * to manipulate data sources and persist schedule like
 * the BaseUpdateService class.
 */
interface UpdateSource {
    fun update()
}