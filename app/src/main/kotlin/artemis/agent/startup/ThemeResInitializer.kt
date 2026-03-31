package artemis.agent.startup

import android.content.Context
import androidx.annotation.StyleRes
import androidx.startup.Initializer
import artemis.agent.R
import artemis.agent.UserSettingsSerializer.userSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeResInitializer : Initializer<Int> {
    override fun create(context: Context): Int = runBlocking {
        themeIndex = context.userSettings.data.first().theme.number
        themeIndex
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    companion object {
        @get:StyleRes
        val splashThemeRes: Int
            get() = SPLASH_THEMES[themeIndex]

        var themeIndex: Int = 0

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
