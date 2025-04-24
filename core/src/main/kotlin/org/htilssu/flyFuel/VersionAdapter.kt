package org.htilssu.flyFuel

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

/**
 * Interface for version-specific functionality
 * This allows the plugin to adapt to API differences between Minecraft versions
 */
interface VersionAdapter {
    /**
     * Get the server version
     * @return the server version as a string
     */
    fun getVersion(): String
    
    /**
     * Register version-specific events and listeners
     * @param plugin the plugin instance
     */
    fun registerEvents(plugin: Plugin)
    
    /**
     * Check if this adapter is compatible with the current server version
     * @param serverVersion the server's version string
     * @return true if this adapter is compatible, false otherwise
     */
    fun isCompatible(serverVersion: String): Boolean
}