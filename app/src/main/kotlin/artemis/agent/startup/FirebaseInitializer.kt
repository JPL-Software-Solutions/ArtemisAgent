package artemis.agent.startup

import android.content.Context
import androidx.startup.Initializer
import artemis.agent.BuildConfig
import artemis.agent.RemoteConfigKey
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.walkertribe.ian.util.Version
import kotlin.time.Duration.Companion.minutes

class FirebaseInitializer : Initializer<FirebaseRemoteConfig> {
    override fun create(context: Context): FirebaseRemoteConfig {
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1.minutes.inWholeSeconds
        }
        return Firebase.remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(
                mapOf(RemoteConfigKey.ARTEMIS_LATEST_VERSION to Version.DEFAULT.toString())
            )
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
