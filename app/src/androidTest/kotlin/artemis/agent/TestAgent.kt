package artemis.agent

import android.app.Application
import io.kotzilla.annotation.KotzillaInternalApi
import io.kotzilla.sdk.KotzillaCore
import io.kotzilla.sdk.KotzillaCoreSDK
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class TestAgent : Application(), KoinStartup {
    override fun onKoinStartup() = koinConfiguration { androidContext(this@TestAgent) }

    @OptIn(KotzillaInternalApi::class)
    override fun onCreate() {
        super.onCreate()
        KotzillaCore.setDefaultInstance(KotzillaCoreSDK())
    }
}
