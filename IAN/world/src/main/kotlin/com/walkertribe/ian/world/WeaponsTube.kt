package com.walkertribe.ian.world

import com.walkertribe.ian.enums.OrdnanceType
import com.walkertribe.ian.enums.TubeState

class WeaponsTube(private val index: Int, timestamp: Long) {
    val state = Property.ObjectProperty<TubeState>("State", timestamp)
    val lastContents = Property.ObjectProperty<OrdnanceType>("Last contents", timestamp)

    var contents: OrdnanceType?
        get() =
            lastContents.value?.takeIf {
                state.value == TubeState.LOADED || state.value == TubeState.LOADING
            }
        set(ordnanceType) {
            lastContents.value = ordnanceType
        }

    val hasData: Boolean
        get() = state.hasValue || lastContents.hasValue

    infix fun updates(tube: WeaponsTube) {
        state updates tube.state
        lastContents updates tube.lastContents
    }

    internal fun appendTo(builder: StringBuilder) {
        if (!state.hasValue) return

        builder.append("\nTube ")
        builder.append(index + 1)
        builder.append(": ")

        contents?.also { ordnance ->
            builder.append(ordnance)
            builder.append(" ")
        }

        builder.append(state.value)
    }
}
