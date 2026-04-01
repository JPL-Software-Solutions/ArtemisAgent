package artemis.agent.startup

import android.content.Context
import androidx.annotation.StyleRes
import androidx.startup.Initializer
import artemis.agent.R
import artemis.agent.UserSettingsSerializer.userSettings
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeResInitializer : Initializer<Int> {
    @OptIn(ExperimentalAtomicApi::class)
    override fun create(context: Context): Int = runBlocking {
        context.userSettings.data.first().themeValue.also(themeIndex::store)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    companion object {
        @OptIn(ExperimentalAtomicApi::class)
        @get:StyleRes
        val splashThemeRes: Int
            get() = SPLASH_THEMES.getOrElse(themeIndex.load()) { SPLASH_THEMES.first() }

        @OptIn(ExperimentalAtomicApi::class) val themeIndex = AtomicInt(0)

        private val SPLASH_THEMES =
            arrayOf(
                R.style.Theme_SplashScreen_ArtemisAgent,
                R.style.Theme_SplashScreen_ArtemisAgent_Red,
                R.style.Theme_SplashScreen_ArtemisAgent_Green,
                R.style.Theme_SplashScreen_ArtemisAgent_Yellow,
                R.style.Theme_SplashScreen_ArtemisAgent_Blue,
                R.style.Theme_SplashScreen_ArtemisAgent_Purple,
                R.style.Theme_SplashScreen_ArtemisAgent_Orange,
            )
    }
}
