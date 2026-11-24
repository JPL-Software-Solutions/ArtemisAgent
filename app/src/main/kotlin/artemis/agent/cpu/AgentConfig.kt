package artemis.agent.cpu

import artemis.agent.UserSettingsKt
import artemis.agent.UserSettingsOuterClass

object AgentConfig {
    var port: Int = AgentConstants.DEFAULT_PORT
    var connectTimeout: Int = AgentConstants.DEFAULT_CONNECT_TIMEOUT
    var scanTimeout: Int = AgentConstants.DEFAULT_SCAN_TIMEOUT
    var showingNetworkInfo: Boolean = false
    var alwaysScanPublicBroadcasts: Boolean = true
    var updateObjectsInterval: Int = AgentConstants.DEFAULT_UPDATE_INTERVAL
    var threeDigitDirections = true

    fun updateFromSettings(settings: UserSettingsOuterClass.UserSettings) {
        port = settings.serverPort
        updateObjectsInterval = settings.updateInterval
        connectTimeout = settings.connectionTimeoutSeconds
        scanTimeout = settings.scanTimeoutSeconds
        showingNetworkInfo = settings.showNetworkInfo
        alwaysScanPublicBroadcasts = settings.alwaysScanPublic
        threeDigitDirections = settings.threeDigitDirections
    }

    fun revertSettings(settings: UserSettingsKt.Dsl) {
        settings.serverPort = port
        settings.updateInterval = updateObjectsInterval
        settings.connectionTimeoutSeconds = connectTimeout
        settings.scanTimeoutSeconds = scanTimeout
        settings.threeDigitDirections = threeDigitDirections
        settings.showNetworkInfo = showingNetworkInfo
        settings.alwaysScanPublic = alwaysScanPublicBroadcasts
    }
}
