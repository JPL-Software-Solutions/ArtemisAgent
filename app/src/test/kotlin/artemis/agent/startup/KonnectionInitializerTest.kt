package artemis.agent.startup

import android.content.Context
import dev.tmapps.konnection.IpResolver
import dev.tmapps.konnection.Konnection
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify

class KonnectionInitializerTest :
    DescribeSpec({
        describe("KonnectionInitializer") {
            val initializer = KonnectionInitializer()

            val mockContext = mockk<Context>()
            val contextSlot = slot<Context>()
            val debugLogSlot = slot<Boolean>()
            val ipResolversSlot = slot<List<IpResolver>>()

            mockkObject(Konnection.Companion)
            every {
                Konnection.createInstance(
                    capture(contextSlot),
                    capture(debugLogSlot),
                    capture(ipResolversSlot),
                )
            } returns mockk()

            afterSpec {
                clearAllMocks()
                unmockkAll()
            }

            describe("Create") {
                val konnection by lazy { initializer.create(mockContext) }

                it("Returns Konnection instance") {
                    konnection.shouldBeInstanceOf<Konnection>()
                    verify(exactly = 1) { Konnection.createInstance(any(), any(), any()) }
                }

                it("Uses context argument") { contextSlot.captured shouldBe mockContext }

                it("Debug logging enabled") { debugLogSlot.captured.shouldBeTrue() }

                it("No IP resolvers") { ipResolversSlot.captured.shouldBeEmpty() }
            }

            it("No dependencies") { initializer.dependencies().shouldBeEmpty() }
        }
    })
