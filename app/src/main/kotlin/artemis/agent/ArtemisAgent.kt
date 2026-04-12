package artemis.agent

import android.app.Application
import io.kotzilla.generated.monitoring
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class ArtemisAgent : Application(), KoinStartup {
    override fun onKoinStartup() = koinConfiguration {
        androidContext(this@ArtemisAgent)
        monitoring()
    }
}
