package org.htilssu.flyFuel

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.htilssu.flyFuel.command.FlyFuelCommand
import org.htilssu.flyFuel.listeners.CountdownListener
import org.htilssu.flyFuel.listeners.PlayerListener

class FlyFuel : JavaPlugin() {
    private lateinit var versionAdapter: VersionAdapter
    private lateinit var fuelManager: FuelManager
    private lateinit var fuelStorage: FuelStorage
    private lateinit var fuelConfig: FuelConfig
    private lateinit var countdownTimer: CountdownTimer
    private lateinit var countdownListener: CountdownListener
    private lateinit var playerListener: PlayerListener

    override fun onEnable() {
        // Save default config
        saveDefaultConfig()
        
        // Initialize version adapter
        try {
            // Khởi tạo version adapter
            versionAdapter = VersionAdapterFactory.getAdapter(logger)
            logger.info("Using version adapter: ${versionAdapter.javaClass.simpleName}")
            
            // Khởi tạo các thành phần
            initializeComponents()
            
            // Đăng ký các lệnh
            registerCommands()
            
            // Đăng ký các listeners
            registerListeners()
            
            // Khởi động bộ đếm ngược
            countdownTimer.start()
            
            // Thêm tất cả người chơi đang online vào danh sách theo dõi
            Bukkit.getOnlinePlayers().forEach { player ->
                countdownTimer.trackPlayer(player)
            }
            
            logger.info("FlyFuel has been enabled successfully!")
            logger.info("Compatible with server version: ${versionAdapter.getVersion()}")
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
        
        // Đăng ký version-specific events
        versionAdapter.registerEvents(this)
    }
    
    /**
     * Get the version adapter being used by the plugin
     * @return the version adapter instance
     */
    fun getVersionAdapter(): VersionAdapter {
        return versionAdapter
    }
    
    /**
     * Lấy FuelManager đang được sử dụng
     * @return FuelManager instance
     */
    fun getFuelManager(): FuelManager {
        return fuelManager
    }
    
    /**
     * Lấy FuelStorage đang được sử dụng
     * @return FuelStorage instance
     */
    fun getFuelStorage(): FuelStorage {
        return fuelStorage
    }
    
    /**
     * Lấy FuelConfig đang được sử dụng
     * @return FuelConfig instance
     */
    fun getFuelConfig(): FuelConfig {
        return fuelConfig
    }
    
    /**
     * Lấy CountdownTimer đang được sử dụng
     * @return CountdownTimer instance
     */
    fun getCountdownTimer(): CountdownTimer {
        return countdownTimer
    }
    
    /**
     * Nạp lại cấu hình plugin
     */
    fun reloadFuelConfig() {
        fuelConfig.load()
        fuelConfig.applyToManager(fuelManager)
        
        // Cập nhật cấu hình cho countdown listener
        countdownListener.setConsumptionRate(fuelConfig.consumptionRate)
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
