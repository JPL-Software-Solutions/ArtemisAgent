package com.walkertribe.ian.enums

import com.walkertribe.ian.world.ArtemisObject

@Suppress("FunctionNaming")
fun OtherMessage.Companion.GoDefend(target: ArtemisObject<*>) = OtherMessage.GoDefend(target.id)
