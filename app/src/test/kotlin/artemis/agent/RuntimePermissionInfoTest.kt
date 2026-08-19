package artemis.agent

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.equals.shouldBeEqual

class RuntimePermissionInfoTest :
    DescribeSpec({
        describe("RuntimePermissionInfo") {
            val permissions =
                arrayOf(
                    "android.permission.POST_NOTIFICATIONS" to R.string.notification_rationale,
                    "android.permission.ACCESS_LOCAL_NETWORK" to R.string.local_network_rationale,
                )

            withData(nameFn = { it.name }, RuntimePermissionInfo.entries) { info ->
                it("Permission name") {
                    info.permission shouldBeEqual permissions[info.ordinal].first
                }

                it("Rationale message") {
                    info.rationaleMessage shouldBeEqual permissions[info.ordinal].second
                }
            }
        }
    })
