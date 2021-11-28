package eu.rtsketo.plugins

import eu.rtsketo.backend.Cache.divAssign
import eu.rtsketo.backend.Time.min

/**
 * rtsketo: 28, November, 2021
 */

fun configureCache() {
    // TODO Replace with your endpoints
    "/api/endpoint" /= 30.min
}