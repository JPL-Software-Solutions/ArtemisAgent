package com.walkertribe.ian.iface

import com.walkertribe.ian.protocol.core.GameOverReasonPacket
import com.walkertribe.ian.protocol.core.GameStartPacket
import com.walkertribe.ian.protocol.core.HeartbeatPacket
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Class responsible for tracking and sending HeartbeatPackets.
 *
 * @author rjwut
 */
class HeartbeatManager(private val iface: ArtemisNetworkInterface) {
    private var lastHeartbeatReceivedTime: Instant = Clock.System.now()
    private var lastHeartbeatSentTime: Instant = Instant.DISTANT_PAST
    private var isLost = false
    private var isAutoSendHeartbeat = true
    private var heartbeatTimeout: Duration = DEFAULT_HEARTBEAT_TIMEOUT.seconds
    private var isActive = false

    /** Sets whether the [HeartbeatManager] should automatically send [HeartbeatPacket]s or not. */
    fun setAutoSendHeartbeat(autoSendHeartbeat: Boolean) {
        isAutoSendHeartbeat = autoSendHeartbeat
    }

    /** Sets the timeout value for listening for HeartbeatPackets. */
    fun setTimeout(timeout: Duration) {
        heartbeatTimeout = timeout
    }

    /** Invoked when a [GameStartPacket] is received from the remote machine. */
    @Listener
    fun onGameStart(packet: GameStartPacket) {
        isActive = true
        resetHeartbeatTimestamp(packet.timestamp)
    }

    /** Invoked when a [GameOverReasonPacket] is received from the remote machine. */
    @Listener
    fun onGameOver(@Suppress("UNUSED_PARAMETER") packet: GameOverReasonPacket) {
        isActive = false
    }

    /** Invoked when a [HeartbeatPacket.Server] is received from the remote machine. */
    @Listener
    fun onHeartbeat(packet: HeartbeatPacket.Server) {
        resetHeartbeatTimestamp(packet.timestamp)
    }

    private fun resetHeartbeatTimestamp(timestamp: Long) {
        lastHeartbeatReceivedTime = Instant.fromEpochMilliseconds(timestamp)
        if (isLost) {
            isLost = false
            iface.sendConnectionEvent(ConnectionEvent.HeartbeatRegained)
        }
    }

    /**
     * Checks to see if we need to send a [ConnectionEvent.HeartbeatLost] event, and sends it if
     * needed.
     */
    fun checkForHeartbeat() {
        if (!isActive || isLost) {
            return
        }
        val elapsed = Clock.System.now() - lastHeartbeatReceivedTime
        if (elapsed >= heartbeatTimeout) {
            isLost = true
            iface.sendConnectionEvent(ConnectionEvent.HeartbeatLost)
        }
    }

    /**
     * Determines whether enough time has elapsed that we need to send a HeartbeatPacket, and sends
     * it if needed. Does nothing if autoSendHeartbeat is set to false.
     */
    fun sendHeartbeatIfNeeded() {
        if (!isAutoSendHeartbeat) {
            return
        }
        val now = Clock.System.now()
        if (now - lastHeartbeatSentTime >= HEARTBEAT_SEND_INTERVAL.seconds) {
            iface.sendPacket(HeartbeatPacket.Client)
            lastHeartbeatSentTime = now
        }
    }

    companion object {
        private const val HEARTBEAT_SEND_INTERVAL = 3
        private const val DEFAULT_HEARTBEAT_TIMEOUT = 15
    }
}
