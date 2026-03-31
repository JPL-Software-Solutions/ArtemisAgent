package artemis.agent.startup

import android.content.Context
import androidx.startup.Initializer
import dev.tmapps.konnection.Konnection

class KonnectionInitializer : Initializer<Konnection> {
    override fun create(context: Context): Konnection =
        Konnection.createInstance(
            context = context,
            enableDebugLog = true,
            ipResolvers = emptyList(),
        )

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
