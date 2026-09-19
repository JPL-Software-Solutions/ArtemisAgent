package com.walkertribe.ian.util

inline fun <reified T> buildSortingComparator(
    vararg comparators: Pair<Comparator<T>, Boolean>
): Comparator<T> {
    val enabledComparators = comparators.mapNotNull { (comparator, isEnabled) ->
        comparator.takeIf { isEnabled }
    }

    return Comparator { t1, t2 ->
        for (comparator in enabledComparators) {
            val comparison = comparator.compare(t1, t2)
            if (comparison != 0) return@Comparator comparison
        }

        0
    }
}
