package artemis.agent

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes

enum class RuntimePermissionInfo {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATIONS {
        override val permission: String = android.Manifest.permission.POST_NOTIFICATIONS

        override val rationaleMessage: Int = R.string.notification_rationale
    },
    @RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
    ACCESS_LOCAL_NETWORK {
        override val permission: String = android.Manifest.permission.ACCESS_LOCAL_NETWORK

        override val rationaleMessage: Int = R.string.local_network_rationale
    };

    abstract val permission: String

    @get:StringRes abstract val rationaleMessage: Int
}
