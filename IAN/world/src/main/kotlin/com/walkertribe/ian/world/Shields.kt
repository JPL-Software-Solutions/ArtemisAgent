package com.walkertribe.ian.world

/** Represents either the front or rear shield strength of a ship or station. */
class Shields(private val name: String, timestamp: Long) {
    /** The percentage of the maximum strength the shield has left. Refreshes automatically. */
    var percentage: Float = Float.NaN
        private set

    /**
     * Whether the shield is damaged, i.e. if its current strength is below its maximum strength.
     */
    var isDamaged: Boolean = false
        private set

    /** The current strength of the shield. Unspecified: Float.NaN */
    val strength = Property.FloatProperty("strength", timestamp).apply { addListener { refresh() } }

    /** The maximum strength of the shield. Unspecified: Float.NaN */
    val maxStrength =
        Property.FloatProperty("max strength", timestamp).apply { addListener { refresh() } }

    val hasData: Boolean
        get() = strength.hasValue || maxStrength.hasValue

    infix fun updates(shields: Shields) {
        strength updates shields.strength
        maxStrength updates shields.maxStrength
    }

    internal fun appendTo(builder: StringBuilder) {
        if (!hasData) return

        with(builder) {
            append(name)
            append(" shields: ")
            append(strength.value)

            if (maxStrength.hasValue) {
                append(" / ")
                append(maxStrength.value)
            }
        }
    }

    private fun refresh() {
        percentage = strength.value / maxStrength.value
        isDamaged = strength < maxStrength
    }
}
