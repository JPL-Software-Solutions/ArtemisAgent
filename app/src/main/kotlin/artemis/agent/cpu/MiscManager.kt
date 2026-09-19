package artemis.agent.cpu

import artemis.agent.game.misc.AudioEntry
import artemis.agent.game.misc.CommsActionEntry
import com.walkertribe.ian.enums.AudioMode
import com.walkertribe.ian.iface.Listener
import com.walkertribe.ian.protocol.core.comm.CommsButtonPacket
import com.walkertribe.ian.protocol.core.comm.IncomingAudioPacket
import java.util.concurrent.CopyOnWriteArraySet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MiscManager {
    val actionsExist: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val audioExists: StateFlow<Boolean>
        field = MutableStateFlow(false)

    private val actionSet = CopyOnWriteArraySet<CommsActionEntry>()
    private val audioSet = CopyOnWriteArraySet<AudioEntry>()

    val actions: StateFlow<List<CommsActionEntry>>
        field = MutableStateFlow(emptyList())

    val audio: StateFlow<List<AudioEntry>>
        field = MutableStateFlow(emptyList())

    val showingAudio: MutableStateFlow<Boolean> by lazy { MutableStateFlow(false) }

    var hasUpdate = false
        private set

    val shouldFlash: Boolean?
        get() = hasUpdate.takeIf { hasData }

    private val hasData: Boolean
        get() = actionsExist.value || audioExists.value

    fun reset() {
        hasUpdate = false
        actionsExist.value = false
        audioExists.value = false
        actionSet.clear()
        audioSet.clear()
        actions.value = emptyList()
        audio.value = emptyList()
    }

    fun resetUpdate() {
        hasUpdate = false
    }

    @Listener
    fun onPacket(packet: CommsButtonPacket) {
        when (val action = packet.action) {
            is CommsButtonPacket.Action.RemoveAll -> {
                actionSet.clear()
                hasUpdate = false
            }
            is CommsButtonPacket.Action.Create -> {
                actionSet.add(CommsActionEntry(action.label))
                actionsExist.value = true
                hasUpdate = true
            }
            is CommsButtonPacket.Action.Remove -> {
                if (!actionSet.removeIf { it.label == action.label }) {
                    return
                }
            }
        }
        actions.value = actionSet.toList()
    }

    @Listener
    fun onPacket(packet: IncomingAudioPacket) {
        val audioMode = packet.audioMode as? AudioMode.Incoming ?: return

        audioSet.add(AudioEntry(packet.audioId, audioMode.title))
        audio.value = audioSet.toList()
        audioExists.value = true
        hasUpdate = true
    }

    fun dismissAudio(entry: AudioEntry) {
        if (audioSet.remove(entry)) {
            audio.value = audioSet.toList()
        }
    }
}
