package com.axiomc.plugins

import com.axiomc.backend.Cache.divAssign
import com.axiomc.backend.Time.min


fun configureCache() {
    // TODO Replace with your endpoints
    "/api/endpoint" /= 30.min
}