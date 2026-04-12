package artemis.agent

import android.app.Application
import android.content.Context
import com.kaspersky.kaspresso.runner.KaspressoRunner

class TestRunner : KaspressoRunner() {
    override fun newApplication(
        loader: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application = super.newApplication(loader, TestAgent::class.java.name, context)
}
