package com.axiomc.plugins

import com.axiomc.backend.Cache.divAssign
import com.axiomc.backend.Time.min


fun configureCache() {
    "/api/GetMerchantList" /= 30.min
}