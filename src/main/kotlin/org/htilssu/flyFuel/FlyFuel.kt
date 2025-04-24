package org.htilssu.flyFuel

import org.bukkit.plugin.java.*
import org.htilssu.flyFuel.command.*
import org.htilssu.flyFuel.listeners.*

class FlyFuel : JavaPlugin() {
    public lateinit var fuelManager: FuelManager
    public lateinit var fuelStorage: FuelStorage
    public lateinit var fuelConfig: FuelConfig
    public lateinit var countdownTimer: CountdownTimer
    public lateinit var countdownListener: CountdownListener
    public lateinit var playerListener: PlayerListener

    override fun onEnable() {
        // Save default config
        saveDefaultConfig()
        
        // Initialize version adapter
        try {

            // Khởi tạo các thành phần
            initializeComponents()
            
            // Đăng ký các lệnh
            registerCommands()
            
            // Đăng ký các listeners
            registerListeners()
            
            // Khởi động bộ đếm ngược
            countdownTimer.start()
            
            logger.info("FlyFuel has been enabled successfully!")
        } catch (e: Exception) {
            logger.severe("Failed to enable FlyFuel: ${e.message}")
            e.printStackTrace()
            server.pluginManager.disablePlugin(this)
        }
    }

    override fun onDisable() {
        // Lưu dữ liệu nhiên liệu của tất cả người chơi
        if (::fuelStorage.isInitialized) {
            fuelStorage.saveAllPlayerData()
        }
        
        // Dừng bộ đếm ngược
        if (::countdownTimer.isInitialized) {
            countdownTimer.stop()
        }
        
        logger.info("FlyFuel has been disabled")
    }
    
    /**
     * Khởi tạo các thành phần của plugin
     */
    private fun initializeComponents() {
        // Khởi tạo fuel manager
        fuelManager = FuelManager()
        
        // Khởi tạo cấu hình
        fuelConfig = FuelConfig(this)
        fuelConfig.load()
        fuelConfig.applyToManager(fuelManager)
        
        // Khởi tạo bộ lưu trữ
        fuelStorage = FuelStorage(this, fuelManager)
        fuelStorage.initialize()
        
        // Khởi tạo bộ đếm ngược
        countdownTimer = CountdownTimer(this)
        
        // Khởi tạo các listeners
        countdownListener = CountdownListener(
            fuelManager,
            fuelConfig.consumptionRate,
            fuelConfig.sprintConsumptionRate,
            fuelConfig.lowFuelThreshold,
            fuelConfig.showActionBar
        )
        
        playerListener = PlayerListener(
            fuelManager,
            countdownTimer,
            fuelStorage,
            fuelConfig
        )
    }
    
    /**
     * Đăng ký các listeners
     */
    private fun registerListeners() {
        // Đăng ký các listeners cho sự kiện
        val pluginManager = server.pluginManager
        pluginManager.registerEvents(countdownListener, this)
        pluginManager.registerEvents(playerListener, this)
        
    }

    /**
     * Nạp lại cấu hình plugin
     */
    fun reloadFuelConfig() {
        fuelConfig.load()
        fuelConfig.applyToManager(fuelManager)
        
        // Cập nhật cấu hình cho countdown listener
        countdownListener.setConsumptionRate(fuelConfig.consumptionRate)
        countdownListener.setSprintConsumptionRate(fuelConfig.sprintConsumptionRate)
        countdownListener.setLowFuelThreshold(fuelConfig.lowFuelThreshold)
        countdownListener.setShowActionBar(fuelConfig.showActionBar)
    }
    
    /**
     * Register all plugin commands
     */
    private fun registerCommands() {
        val flyFuelCommand = FlyFuelCommand(this)
        val ffCommand = getCommand("ff")
        
        ffCommand?.setExecutor(flyFuelCommand)
        ffCommand?.tabCompleter = flyFuelCommand
    }
}
