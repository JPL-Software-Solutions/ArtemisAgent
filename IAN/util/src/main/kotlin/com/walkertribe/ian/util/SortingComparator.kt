package com.walkertribe.ian.util

inline fun <reified T> buildSortingComparator(
    vararg comparators: Pair<Comparator<T>, Boolean>
): Comparator<T> {
    val enabledComparators =
        buildList(comparators.size) {
            for ((comparator, isEnabled) in comparators) {
                if (isEnabled) add(comparator)
            }
        }

    return Comparator { t1, t2 ->
        for (comparator in enabledComparators) {
            val comparison = comparator.compare(t1, t2)
            if (comparison != 0) return@Comparator comparison
        }

        0
    }
}
