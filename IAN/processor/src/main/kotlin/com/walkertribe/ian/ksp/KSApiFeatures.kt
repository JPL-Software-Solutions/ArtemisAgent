package com.walkertribe.ian.ksp

import com.google.devtools.ksp.KSApiFeatures

val KSApiFeatures.Companion.ENABLE_ALL by lazy {
    KSApiFeatures(
        enableBackingFields = true,
        enableContextParameters = true,
    )
}
