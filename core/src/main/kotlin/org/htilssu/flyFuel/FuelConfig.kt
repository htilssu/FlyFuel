package org.htilssu.flyFuel

import org.bukkit.plugin.*

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
     * Lượng nhiên liệu mặc định khi người chơi tham gia lần đầu
     */
    var defaultFuel: Double = 50.0
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
    var lowFuelThreshold: Double = 20.0
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
     * Tự động tắt bay khi hết nhiên liệu
     */
    var autoDisableFlight: Boolean = true
        private set
    
    /**
     * Tự động kích hoạt bay khi nạp nhiên liệu (nếu đủ quyền)
     */
    var autoEnableFlight: Boolean = true
        private set
    
    /**
     * Thời gian (tính bằng giây) giữa các lần giảm nhiên liệu
     */
    var countdownInterval: Int = 1
        private set
    
    /**
     * Tin nhắn khi người chơi bắt đầu bay
     */
    var flightEnabledMessage: String = "&aFlight mode activated! Fuel: &f%fuel%"
        private set
    
    /**
     * Tin nhắn khi người chơi dừng bay
     */
    var flightDisabledMessage: String = "&cFlight mode deactivated! Remaining fuel: &f%fuel%"
        private set
    
    /**
     * Tin nhắn khi người chơi hết nhiên liệu
     */
    var outOfFuelMessage: String = "&cYou are out of flight fuel!"
        private set
    
    /**
     * Định dạng tin nhắn cảnh báo nhiên liệu thấp
     */
    var lowFuelMessage: String = "&eFlight fuel is running low! Remaining: &f%fuel%"
        private set
    
    /**
     * Định dạng hiển thị nhiên liệu trên thanh action bar
     */
    var fuelDisplayFormat: String = "&6Flight fuel: &f%fuel%/%max%"
        private set
    
    /**
     * Định dạng hiển thị thanh nhiên liệu
     */
    var fuelBarFormat: String = "&6Fuel: {bar} &f{fuel}/{maxFuel}"
        private set
    
    /**
     * Tin nhắn khi nạp nhiên liệu thành công
     */
    var refuelSuccessMessage: String = "&aRefueled successfully! Current fuel: &f%fuel%/%max%"
        private set
    
    /**
     * Nạp cấu hình từ file config.yml
     */
    fun load() {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        val config = plugin.config
        
        // Basic settings
        countdownInterval = config.getInt("settings.countdown-interval", 1)
        consumptionRate = config.getDouble("settings.consumption-rate", 1.0)
        sprintConsumptionRate = config.getDouble("settings.sprint-consumption-rate", 2.0)
        maxFuel = config.getDouble("settings.max-fuel", 100.0)
        defaultFuel = config.getDouble("settings.default-fuel", 50.0)
        lowFuelThreshold = config.getDouble("settings.low-fuel-threshold", 20.0)
        autoDisableFlight = config.getBoolean("settings.auto-disable-flight", true)
        autoEnableFlight = config.getBoolean("settings.auto-enable-flight", true)
        saveOnQuit = config.getBoolean("settings.save-on-quit", true)
        refillOnJoin = config.getBoolean("settings.refill-on-join", false)
        
        // Messages
        showActionBar = config.getBoolean("messages.show-action-bar", true)
        flightEnabledMessage = config.getString("messages.flight-enabled", flightEnabledMessage) ?: flightEnabledMessage
        flightDisabledMessage = config.getString("messages.flight-disabled", flightDisabledMessage) ?: flightDisabledMessage
        outOfFuelMessage = config.getString("messages.out-of-fuel", outOfFuelMessage) ?: outOfFuelMessage
        lowFuelMessage = config.getString("messages.low-fuel", lowFuelMessage) ?: lowFuelMessage
        fuelDisplayFormat = config.getString("messages.fuel-display", fuelDisplayFormat) ?: fuelDisplayFormat
        refuelSuccessMessage = config.getString("messages.refuel-success", refuelSuccessMessage) ?: refuelSuccessMessage
        fuelBarFormat = config.getString("messages.fuel-bar-format", fuelBarFormat) ?: fuelBarFormat
    }
    
    /**
     * Lưu cấu hình vào file config.yml
     */
    fun save() {
        val config = plugin.config
        
        // Basic settings
        config.set("settings.countdown-interval", countdownInterval)
        config.set("settings.consumption-rate", consumptionRate)
        config.set("settings.sprint-consumption-rate", sprintConsumptionRate)
        config.set("settings.max-fuel", maxFuel)
        config.set("settings.default-fuel", defaultFuel)
        config.set("settings.low-fuel-threshold", lowFuelThreshold)
        config.set("settings.auto-disable-flight", autoDisableFlight)
        config.set("settings.auto-enable-flight", autoEnableFlight)
        config.set("settings.save-on-quit", saveOnQuit)
        config.set("settings.refill-on-join", refillOnJoin)
        
        // Messages
        config.set("messages.show-action-bar", showActionBar)
        config.set("messages.flight-enabled", flightEnabledMessage)
        config.set("messages.flight-disabled", flightDisabledMessage)
        config.set("messages.out-of-fuel", outOfFuelMessage)
        config.set("messages.low-fuel", lowFuelMessage)
        config.set("messages.fuel-display", fuelDisplayFormat)
        config.set("messages.refuel-success", refuelSuccessMessage)
        config.set("messages.fuel-bar-format", fuelBarFormat)
        
        plugin.saveConfig()
    }
    
    /**
     * Áp dụng cấu hình vào FuelManager
     * 
     * @param fuelManager FuelManager cần áp dụng cấu hình
     */
    fun applyToManager(fuelManager: FuelManager) {
        fuelManager.maxFuel = maxFuel
        fuelManager.defaultFuel = defaultFuel
        fuelManager.defaultConsumptionRate = consumptionRate
        fuelManager.sprintConsumptionRate = sprintConsumptionRate
        fuelManager.autoDisableFlight = autoDisableFlight
        fuelManager.autoEnableFlight = autoEnableFlight
    }
} 