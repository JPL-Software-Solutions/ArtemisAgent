package com.walkertribe.ian.util

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.classesAndInterfaces
import com.lemonappdev.konsist.api.ext.list.objects
import com.lemonappdev.konsist.api.ext.list.withRepresentedTypeOf
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeSameSizeAs
import withoutCompanionModifier

class BoolStateKonsistTest :
    DescribeSpec({
        describe("BoolState") {
            val matchingClasses =
                Konsist.scopeFromModule("IAN/util")
                    .classes()
                    .withRepresentedTypeOf(BoolState::class)
            val stateObjects = matchingClasses.objects().withoutCompanionModifier()
            val expectedNames = setOf("True", "False", "Unknown")

            it("Only has object members") { matchingClasses.classesAndInterfaces().shouldBeEmpty() }

            describe("Members") {
                it("Count: ${expectedNames.size}") { stateObjects shouldBeSameSizeAs expectedNames }

                withData(nameFn = { it.name }, stateObjects) { state ->
                    state.assertTrue { expectedNames.contains(it.name) }
                }
            }
        }
    })
