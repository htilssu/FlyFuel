package org.htilssu.flyFuel

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin

/**
 * Quản lý cấu hình của plugin FlyFuel
 */
class FuelConfig(private val plugin: Plugin) {
    
    /**
     * Lượng nhiên liệu tối đa mặc định cho người chơi
     */
    var maxFuel: Double = 100.0
        private set
    
    /**
     * Tốc độ tiêu hao nhiên liệu mặc định (đơn vị/giây)
     */
    var consumptionRate: Double = 1.0
        private set
    
    /**
     * Tốc độ tiêu hao nhiên liệu khi chạy nước rút (đơn vị/giây)
     */
    var sprintConsumptionRate: Double = 2.0
        private set
    
    /**
     * Ngưỡng cảnh báo khi nhiên liệu thấp
     */
    var lowFuelThreshold: Double = 10.0
        private set
    
    /**
     * Có lưu dữ liệu khi người chơi thoát không
     */
    var saveOnQuit: Boolean = true
        private set
    
    /**
     * Tự động nạp lại nhiên liệu khi người chơi tham gia lại
     */
    var refillOnJoin: Boolean = false
        private set
    
    /**
     * Hiển thị thông báo nhiên liệu trên action bar
     */
    var showActionBar: Boolean = true
        private set
    
    /**
     * Tin nhắn khi người chơi hết nhiên liệu
     */
    var outOfFuelMessage: String = "&cBạn đã hết nhiên liệu!"
        private set
    
    /**
     * Định dạng tin nhắn cảnh báo nhiên liệu thấp
     */
    var lowFuelMessage: String = "&eCảnh báo: Nhiên liệu còn thấp ({fuel})!"
        private set
    
    /**
     * Định dạng hiển thị thanh nhiên liệu
     */
    var fuelBarFormat: String = "&6Nhiên liệu: {bar} &f{fuel}/{maxFuel}"
        private set
    
    /**
     * Nạp cấu hình từ file config.yml
     */
    fun load() {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        val config = plugin.config
        
        maxFuel = config.getDouble("fuel.max", 100.0)
        consumptionRate = config.getDouble("fuel.consumption-rate", 1.0)
        sprintConsumptionRate = config.getDouble("fuel.sprint-consumption-rate", 2.0)
        lowFuelThreshold = config.getDouble("fuel.low-threshold", 10.0)
        
        saveOnQuit = config.getBoolean("settings.save-on-quit", true)
        refillOnJoin = config.getBoolean("settings.refill-on-join", false)
        showActionBar = config.getBoolean("settings.show-action-bar", true)
        
        outOfFuelMessage = config.getString("messages.out-of-fuel", outOfFuelMessage) ?: outOfFuelMessage
        lowFuelMessage = config.getString("messages.low-fuel", lowFuelMessage) ?: lowFuelMessage
        fuelBarFormat = config.getString("messages.fuel-bar-format", fuelBarFormat) ?: fuelBarFormat
    }
    
    /**
     * Lưu cấu hình vào file config.yml
     */
    fun save() {
        val config = plugin.config
        
        config.set("fuel.max", maxFuel)
        config.set("fuel.consumption-rate", consumptionRate)
        config.set("fuel.sprint-consumption-rate", sprintConsumptionRate)
        config.set("fuel.low-threshold", lowFuelThreshold)
        
        config.set("settings.save-on-quit", saveOnQuit)
        config.set("settings.refill-on-join", refillOnJoin)
        config.set("settings.show-action-bar", showActionBar)
        
        config.set("messages.out-of-fuel", outOfFuelMessage)
        config.set("messages.low-fuel", lowFuelMessage)
        config.set("messages.fuel-bar-format", fuelBarFormat)
        
        plugin.saveConfig()
    }
    
    /**
     * Tạo file cấu hình mặc định nếu chưa tồn tại
     */
    fun createDefaultConfig() {
        val defaultConfig = plugin.getResource("config.yml")
        if (defaultConfig == null) {
            // Nếu file config mặc định không tồn tại trong resources, tạo file mới
            val config = plugin.config
            
            config.options().header("""
                FlyFuel Plugin Configuration
                ---------------------------
                Cấu hình plugin FlyFuel
                
                fuel.max: Lượng nhiên liệu tối đa cho mỗi người chơi
                fuel.consumption-rate: Tốc độ tiêu hao nhiên liệu khi đi bộ (đơn vị/giây)
                fuel.sprint-consumption-rate: Tốc độ tiêu hao nhiên liệu khi chạy nước rút (đơn vị/giây)
                fuel.low-threshold: Ngưỡng cảnh báo khi nhiên liệu thấp
                
                settings.save-on-quit: Lưu dữ liệu khi người chơi thoát
                settings.refill-on-join: Tự động nạp lại nhiên liệu khi người chơi tham gia lại
                settings.show-action-bar: Hiển thị thông báo nhiên liệu trên action bar
                
                messages.out-of-fuel: Tin nhắn khi người chơi hết nhiên liệu
                messages.low-fuel: Định dạng tin nhắn cảnh báo nhiên liệu thấp
                messages.fuel-bar-format: Định dạng hiển thị thanh nhiên liệu
            """.trimIndent())
            
            save()
        }
    }
    
    /**
     * Áp dụng cấu hình vào FuelManager
     * 
     * @param fuelManager FuelManager cần áp dụng cấu hình
     */
    fun applyToManager(fuelManager: FuelManager) {
        fuelManager.maxFuel = maxFuel
        fuelManager.defaultConsumptionRate = consumptionRate
    }
    
    /**
     * Áp dụng cấu hình vào FuelTask
     * 
     * @param fuelTask FuelTask cần áp dụng cấu hình
     */
    fun applyToTask(fuelTask: FuelTask) {
        fuelTask.lowFuelThreshold = lowFuelThreshold
    }
} 