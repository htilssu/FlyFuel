package org.htilssu.flyFuel.version

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

/**
 * Interface for version-specific functionality
 * This allows the plugin to adapt to API differences between Minecraft versions
 */
interface VersionAdapter {
    /**
     * Get the server's NMS version string
     * @return the NMS version string
     */
    fun getServerNMSVersion(): String
    
    /**
     * Check if a player is flying
     * @param player the player to check
     * @return true if the player is flying, false otherwise
     */
    fun isPlayerFlying(player: Player): Boolean
    
    /**
     * Set the player's flying state
     * @param player the player to set flying state for
     * @param flying true to enable flying, false to disable
     */
    fun setPlayerFlying(player: Player, flying: Boolean)
    
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