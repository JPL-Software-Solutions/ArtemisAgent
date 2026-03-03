package artemis.agent

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.equals.shouldBeEqual

class RuntimePermissionInfoTest :
    DescribeSpec({
        describe("RuntimePermissionInfo") {
            val rationaleResIds =
                intArrayOf(R.string.notification_rationale, R.string.local_network_rationale)
            val permissionNames =
                arrayOf(
                    "android.permission.POST_NOTIFICATIONS",
                    "android.permission.ACCESS_LOCAL_NETWORK",
                )

            withData(nameFn = { it.name }, RuntimePermissionInfo.entries) { info ->
                it("Permission name") {
                    info.permission shouldBeEqual permissionNames[info.ordinal]
                }

                it("Rationale message") {
                    info.rationaleMessage shouldBeEqual rationaleResIds[info.ordinal]
                }
            }
        }
    })
