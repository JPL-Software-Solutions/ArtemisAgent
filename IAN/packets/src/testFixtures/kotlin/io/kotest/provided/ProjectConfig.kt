package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotzilla.annotation.KotzillaInternalApi
import io.kotzilla.sdk.KotzillaCore
import io.kotzilla.sdk.KotzillaCoreSDK

@OptIn(KotzillaInternalApi::class)
object ProjectConfig : AbstractProjectConfig() {
    init {
        KotzillaCore.setDefaultInstance(KotzillaCoreSDK())
    }
}
