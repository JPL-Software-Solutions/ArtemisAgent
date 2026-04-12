package com.walkertribe.ian.world

import com.walkertribe.ian.util.BoolState
import com.walkertribe.ian.util.isKnown

sealed class Property<V, P : Property<V, P>>(
    internal val name: String,
    initialValue: V,
    initialTimestamp: Long,
    private var onSet: (V) -> Unit = {},
) {
    class FloatProperty(name: String, timestamp: Long, onSet: (Float) -> Unit = {}) :
        Property<Float, FloatProperty>(name, Float.NaN, timestamp, onSet),
        Comparable<FloatProperty> {
        override val hasValue: Boolean
            get() = !value.isNaN()

        val valueOrZero: Float
            get() = if (value.isNaN()) 0f else value

        override fun compareTo(other: FloatProperty): Int =
            when {
                !other.hasValue -> if (hasValue) 1 else 0
                hasValue -> compareValuesBy(this, other) { it.value }
                else -> -1
            }
    }

    class ByteProperty(
        name: String,
        timestamp: Long,
        private val initialValue: Byte = -1,
        onSet: (Byte) -> Unit = {},
    ) :
        Property<Byte, ByteProperty>(name, initialValue, timestamp, onSet),
        Comparable<ByteProperty> {
        override val hasValue: Boolean
            get() = value != initialValue

        override fun compareTo(other: ByteProperty): Int =
            when {
                !other.hasValue -> if (hasValue) 1 else 0
                hasValue -> compareValuesBy(this, other) { it.value }
                else -> -1
            }

        override fun checkCanUpdate(property: ByteProperty) {
            require(initialValue == property.initialValue) {
                "Property could not be updated: initial value mismatch; " +
                    "expected $initialValue, found ${property.initialValue}"
            }
        }
    }

    class IntProperty(
        name: String,
        timestamp: Long,
        private val initialValue: Int = -1,
        onSet: (Int) -> Unit = {},
    ) : Property<Int, IntProperty>(name, initialValue, timestamp, onSet), Comparable<IntProperty> {
        override val hasValue: Boolean
            get() = value != initialValue

        override fun compareTo(other: IntProperty): Int =
            when {
                !other.hasValue -> if (hasValue) 1 else 0
                hasValue -> compareValuesBy(this, other) { it.value }
                else -> -1
            }

        override fun checkCanUpdate(property: IntProperty) {
            require(initialValue == property.initialValue) {
                "Property could not be updated: initial value mismatch; " +
                    "expected $initialValue, found ${property.initialValue}"
            }
        }
    }

    class BoolProperty(name: String, timestamp: Long, onSet: (BoolState) -> Unit = {}) :
        Property<BoolState, BoolProperty>(name, BoolState.Unknown, timestamp, onSet) {
        override val hasValue: Boolean
            get() = value.isKnown
    }

    class ObjectProperty<V : Any>(name: String, timestamp: Long, onSet: (V?) -> Unit = {}) :
        Property<V?, ObjectProperty<V>>(name, null, timestamp, onSet) {
        override val hasValue: Boolean
            get() = value != null
    }

    var value: V = initialValue
        set(newValue) {
            field = newValue
            onSet(newValue)
        }

    private var timestamp: Long = initialTimestamp

    abstract val hasValue: Boolean

    internal fun addListener(onSet: (V) -> Unit) {
        this.onSet = onSet
    }

    protected open fun checkCanUpdate(property: P) {}

    infix fun updates(property: P) {
        updates(property) {}
    }

    internal inline fun updates(property: P, ifNotUpdated: () -> Unit) {
        synchronized(property) {
            checkCanUpdate(property)

            if (timestamp < property.timestamp) {
                return
            }

            if (hasValue) {
                property.timestamp = timestamp
                property.value = value
            } else {
                ifNotUpdated()
            }
        }
    }

    internal fun appendTo(builder: StringBuilder) {
        if (hasValue) {
            builder.append("\n")
            builder.append(name)
            builder.append(": ")
            builder.append(value)
        }
    }

    override fun toString(): String = value.toString()
}
