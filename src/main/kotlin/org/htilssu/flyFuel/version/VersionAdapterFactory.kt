package org.htilssu.flyFuel.version

import org.bukkit.Bukkit
import org.htilssu.flyFuel.version.impl.v1_18.VersionAdapter_1_18
import org.htilssu.flyFuel.version.impl.v1_19.VersionAdapter_1_19
import org.htilssu.flyFuel.version.impl.v1_20.VersionAdapter_1_20
import java.util.logging.Logger

/**
 * Factory class to create the appropriate VersionAdapter for the current server version
 */
object VersionAdapterFactory {
    private val adapters = listOf(
        VersionAdapter_1_18(),
        VersionAdapter_1_19(),
        VersionAdapter_1_20()
    )
    
    /**
     * Get the appropriate VersionAdapter for the current server version
     * @param logger Logger to use for logging
     * @return the appropriate VersionAdapter
     * @throws IllegalStateException if no compatible adapter is found
     */
    fun getAdapter(logger: Logger): VersionAdapter {
        val version = getServerVersion()
        logger.info("Detected server version: $version")
        
        val adapter = adapters.find { it.isCompatible(version) }
        
        return adapter ?: throw IllegalStateException(
            "Unsupported server version: $version. " +
            "This plugin supports versions: ${adapters.joinToString(", ") { it.getServerNMSVersion() }}"
        )
    }
    
    /**
     * Get the server's version string
     * @return the server's version string (e.g. "1.18.2")
     */
    private fun getServerVersion(): String {
        return Bukkit.getBukkitVersion().split("-")[0]
    }
}