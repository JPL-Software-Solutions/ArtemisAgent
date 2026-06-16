package com.walkertribe.ian.world

import com.walkertribe.ian.iface.ListenerArgument
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.mockk.clearMocks
import io.mockk.mockk

class ArtemisObjectTest : DescribeSpec() {
    init {
        arrayOf(
                ObjectTestSuite.Base,
                ObjectTestSuite.BlackHole,
                ObjectTestSuite.Creature,
                ObjectTestSuite.Mine,
                ObjectTestSuite.Npc,
                ObjectTestSuite.Player,
            )
            .withEach { createTests() }

        describe("ArtemisObjectListenerModule") {
            val mockArgument = mockk<ListenerArgument>()

            it("Does not accept arguments that are not objects") {
                ArtemisObjectTestModule.onArtemisObject(mockArgument)
                ArtemisObjectTestModule.collected.shouldBeEmpty()
            }

            clearMocks(mockArgument)
        }
    }

    private companion object {
        inline fun <T> Array<T>.withEach(crossinline block: T.() -> Unit) {
            for (element in this) element.block()
        }
    }
}
