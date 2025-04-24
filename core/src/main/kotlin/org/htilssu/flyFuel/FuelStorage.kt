package org.htilssu.flyFuel

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.*

/**
 * Lưu trữ dữ liệu nhiên liệu của người chơi
 */
class FuelStorage(
    private val plugin: Plugin,
    private val fuelManager: FuelManager
) {
    /**
     * File chứa dữ liệu nhiên liệu
     */
    private val dataFile: File = File(plugin.dataFolder, "data.yml")
    
    /**
     * Cấu hình YAML để đọc/ghi dữ liệu
     */
    private lateinit var dataConfig: YamlConfiguration
    
    /**
     * Khởi tạo kho lưu trữ, tạo file dữ liệu nếu cần
     */
    fun initialize() {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }
        
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile()
            } catch (e: Exception) {
                plugin.logger.severe("Không thể tạo file dữ liệu: ${e.message}")
                e.printStackTrace()
            }
        }
        
        loadData()
    }
    
    /**
     * Đọc dữ liệu từ file
     */
    fun loadData() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile)
    }
    
    /**
     * Lưu dữ liệu vào file
     */
    fun saveData() {
        try {
            dataConfig.save(dataFile)
        } catch (e: Exception) {
            plugin.logger.severe("Không thể lưu dữ liệu: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Nạp dữ liệu nhiên liệu cho người chơi khi họ tham gia server
     * 
     * @param player Người chơi cần nạp dữ liệu
     */
    fun loadPlayerData(player: Player) {
        val uuid = player.uniqueId.toString()
        if (dataConfig.contains("players.$uuid.fuel")) {
            val fuel = dataConfig.getDouble("players.$uuid.fuel")
            fuelManager.setFuel(player, fuel)
        }
    }
    
    /**
     * Lưu dữ liệu nhiên liệu của người chơi khi họ thoát khỏi server
     * 
     * @param player Người chơi cần lưu dữ liệu
     */
    fun savePlayerData(player: Player) {
        val uuid = player.uniqueId.toString()
        val fuel = fuelManager.getFuel(player)
        
        dataConfig.set("players.$uuid.name", player.name)
        dataConfig.set("players.$uuid.fuel", fuel)
        dataConfig.set("players.$uuid.last_seen", System.currentTimeMillis())
        
        saveData()
    }
    
    /**
     * Lưu dữ liệu của tất cả người chơi đang online
     */
    fun saveAllPlayerData() {
        plugin.server.onlinePlayers.forEach { player ->
            savePlayerData(player)
        }
    }
    
    /**
     * Xóa dữ liệu của người chơi
     * 
     * @param player Người chơi cần xóa dữ liệu
     */
    fun removePlayerData(player: Player) {
        val uuid = player.uniqueId.toString()
        dataConfig.set("players.$uuid", null)
        saveData()
    }
    
    /**
     * Xóa dữ liệu của tất cả người chơi
     */
    fun clearAllData() {
        dataConfig.set("players", null)
        saveData()
    }
} 