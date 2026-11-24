package artemis.agent.cpu

import android.content.Context
import android.media.MediaPlayer
import artemis.agent.UserSettingsKt
import artemis.agent.UserSettingsOuterClass
import artemis.agent.util.SoundEffect

class SoundManager(context: Context) {
    var isMuted: Boolean = false
    var volume: Float = 1f
        set(value) {
            field = value / VOLUME_SCALE
        }

    private val playSounds: Boolean
        get() = volume > 0f && !isMuted

    private val sounds: MutableList<MediaPlayer?> =
        SoundEffect.entries.map { MediaPlayer.create(context, it.soundId) }.toMutableList()

    /** Plays a sound effect if sound effects are enabled. */
    fun play(sound: SoundEffect) {
        if (playSounds) {
            sounds[sound.ordinal]?.also { player ->
                player.setVolume(volume, volume)
                player.start()
            }
        }
    }

    fun updateFromSettings(settings: UserSettingsOuterClass.UserSettings) {
        volume = settings.soundVolume.toFloat()
        isMuted = settings.soundMuted
    }

    fun revertSettings(settings: UserSettingsKt.Dsl) {
        settings.soundVolume = (volume * VOLUME_SCALE).toInt()
        settings.soundMuted = isMuted
    }

    fun dispose() {
        volume = 0f
        sounds.forEach { it?.release() }
        sounds.clear()
    }

    companion object {
        const val VOLUME_SCALE = 100f
    }
}
