package org.htilssu.flyFuel.v1_18

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.htilssu.flyFuel.VersionAdapter

/**
 * Version adapter for Minecraft 1.18.x
 */
class VersionAdapter_1_18 : VersionAdapter {
    override fun getVersion(): String = "v1_18_R1"
    
    override fun registerEvents(plugin: Plugin) {
        // Register any 1.18 specific events here
    }
    
    override fun isCompatible(serverVersion: String): Boolean {
        return serverVersion.startsWith("1.18")
    }
}