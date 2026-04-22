package artemis.agent.util

import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BackPreview(enabled: Boolean) : OnBackPressedCallback(enabled) {
    private val isPreviewingMutable: MutableStateFlow<Boolean> by lazy { MutableStateFlow(false) }
    val isPreviewing: StateFlow<Boolean>
        get() = isPreviewingMutable.asStateFlow()

    abstract fun preview()

    abstract fun revert()

    protected open fun beforePreview() {}

    protected open fun close() {}

    fun onBackStarted() {
        beforePreview()
        preview()
        isPreviewingMutable.value = true
    }

    final override fun handleOnBackStarted(backEvent: BackEventCompat) {
        onBackStarted()
    }

    final override fun handleOnBackProgressed(backEvent: BackEventCompat) {
        if (backEvent.progress > 0f) {
            preview()
            isPreviewingMutable.value = true
        } else {
            revert()
            isPreviewingMutable.value = false
        }
    }

    final override fun handleOnBackCancelled() {
        revert()
        close()
        isPreviewingMutable.value = false
    }

    final override fun handleOnBackPressed() {
        isEnabled = false
        preview()
        close()
        isPreviewingMutable.value = false
    }
}
