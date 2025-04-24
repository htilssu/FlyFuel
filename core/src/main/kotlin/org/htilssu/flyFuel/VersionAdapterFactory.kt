package org.htilssu.flyFuel

import org.bukkit.*
import java.util.*
import java.util.logging.*

/**
 * Factory class to create the appropriate VersionAdapter for the current server version
 */
object VersionAdapterFactory {
    /**
     * Get the appropriate VersionAdapter for the current server version
     * @param logger Logger to use for logging
     * @return the appropriate VersionAdapter
     * @throws IllegalStateException if no compatible adapter is found
     */
    fun getAdapter(logger: Logger): VersionAdapter {
        val version = getServerVersion()
        logger.info("Detected server version: $version")
        
        // Use Java's ServiceLoader to dynamically load all adapter implementations
        val adapters = ServiceLoader.load(VersionAdapter::class.java).toList()
        logger.info("Found ${adapters.size} version adapters")
        
        // Find a compatible adapter
        val adapter = adapters.find { it.isCompatible(version) }
        
        return adapter
            ?: throw IllegalStateException("No compatible version adapter found for version $version")
    }
    
    /**
     * Get the server's version string
     * @return the server's version string (e.g. "1.18.2")
     */
    private fun getServerVersion(): String {
        return Bukkit.getBukkitVersion().split("-")[0]
    }
}