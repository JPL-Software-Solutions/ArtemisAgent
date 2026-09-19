package artemis.agent.setup

import android.content.Context
import android.view.LayoutInflater
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll

class RecentServersAdapterTest :
    DescribeSpec({
        describe("RecentServersAdapter") {
            val arbString = Arb.string()
            val arbSize = Arb.int(10..30)

            val size = arbSize.next()
            val servers = List(size) { arbString.next() }

            val mockContext =
                mockk<Context> { every { getSystemService(any()) } returns mockk<LayoutInflater>() }

            mockkConstructor(ConnectFragment.RecentServersFilter::class)

            every { anyConstructed<ConnectFragment.RecentServersFilter>().suggestions } returns
                servers

            val adapter = ConnectFragment.RecentServersAdapter(mockContext)

            afterSpec {
                clearAllMocks()
                unmockkAll()
            }

            it("Filter is defined") {
                adapter.filter.shouldBeInstanceOf<ConnectFragment.RecentServersFilter>()
            }

            it("Item count") { adapter.count shouldBeEqual size }

            it("Items by position") {
                repeat(size) { position ->
                    adapter.getItem(position) shouldBeEqual servers[position]
                }
            }
        }
    })
