package artemis.agent.setup

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll

class RecentServersFilterTest :
    DescribeSpec({
        describe("RecentServersFilter") {
            var adapterHasData = false

            val mockAdapter =
                mockk<ConnectFragment.RecentServersAdapter> {
                    every { notifyDataSetChanged() } answers { adapterHasData = true }
                    every { notifyDataSetInvalidated() } answers { adapterHasData = false }
                }

            val recentServers =
                listOf(
                    "example.com",
                    "sample.net",
                    "test1.org",
                    "fake.server.co.uk",
                    "10.123.456.789",
                    "10.100.23.79",
                    "10.89.146.12",
                )

            val emptyConstraints = listOf("q", "2.2", "....", "?", "&", "*")

            val partialConstraints =
                listOf(
                    "e" to intArrayOf(0, 1, 2, 3),
                    "xa" to intArrayOf(0),
                    "ample" to intArrayOf(0, 1),
                    "s" to intArrayOf(1, 2, 3),
                    "o" to intArrayOf(0, 2, 3),
                    "e.co" to intArrayOf(0, 3),
                    "e.o" to intArrayOf(0, 2, 3),
                    "a.o" to intArrayOf(0, 3),
                    "e.e" to intArrayOf(1, 3),
                    "1" to intArrayOf(2, 4, 5, 6),
                    "12" to intArrayOf(4, 6),
                    "23" to intArrayOf(4, 5),
                    "14" to intArrayOf(6),
                    "1.23" to intArrayOf(4, 5),
                    "1.3" to intArrayOf(4, 5),
                    "4.1" to intArrayOf(6),
                    "1.9" to intArrayOf(4, 5, 6),
                    "1.1.9" to intArrayOf(4, 5),
                    ".." to intArrayOf(3, 4, 5, 6),
                )

            val serverFilter = ConnectFragment.RecentServersFilter(mockAdapter)
            serverFilter.servers = recentServers

            afterSpec {
                clearAllMocks()
                unmockkAll()
            }

            describe("Perform filtering") {
                it("Blank constraints") {
                    listOf(null, "").forEach { constraint ->
                        serverFilter.doPerformFiltering(constraint) shouldBeEqual recentServers
                    }
                }

                it("Partial results") {
                    partialConstraints.forEach { (constraint, expectedIndices) ->
                        val results = serverFilter.doPerformFiltering(constraint)
                        val expectedResults = expectedIndices.map { recentServers[it] }
                        results shouldBeEqual expectedResults
                    }
                }

                it("Empty results") {
                    emptyConstraints.forEach { constraint ->
                        serverFilter.doPerformFiltering(constraint).shouldBeEmpty()
                    }
                }
            }

            describe("Publish results") {
                it("Full results") {
                    serverFilter.doPublishResults(recentServers, recentServers.size)
                    serverFilter.suggestions shouldBeEqual recentServers
                    adapterHasData.shouldBeTrue()
                }

                it("Empty list") {
                    serverFilter.doPublishResults(emptyList(), 0)
                    serverFilter.suggestions.shouldBeEmpty()
                    adapterHasData.shouldBeFalse()
                }

                it("Partial list") {
                    val count = 2
                    serverFilter.doPublishResults(recentServers.take(count), count)
                    serverFilter.suggestions shouldHaveSize count
                    adapterHasData.shouldBeTrue()
                }
            }
        }
    })
