package artemis.agent.startup

import android.content.Context
import androidx.annotation.StyleRes
import androidx.startup.Initializer
import artemis.agent.R
import artemis.agent.UserSettingsSerializer.userSettings
import kotlin.coroutines.CoroutineContext
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ThemeResInitializer : Initializer<Unit>, CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    override fun create(context: Context) {
        launch { context.userSettings.data.first().themeValue.also(themeIndex::lazySet) }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    companion object {
        @get:StyleRes
        val splashThemeRes: Int
            get() = SPLASH_THEMES.getOrElse(themeIndex.value) { SPLASH_THEMES.first() }

        val themeIndex = atomic(0)

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
