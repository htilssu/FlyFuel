package org.htilssu.flyFuel.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.htilssu.flyFuel.CountdownTimer
import org.htilssu.flyFuel.FuelConfig
import org.htilssu.flyFuel.FuelManager
import org.htilssu.flyFuel.FuelStorage

/**
 * Listener xử lý các sự kiện liên quan đến người chơi
 */
class PlayerListener(
    /**
     * FuelManager để quản lý nhiên liệu
     */
    private val fuelManager: FuelManager,
    
    /**
     * CountdownTimer để quản lý đếm ngược
     */
    private val countdownTimer: CountdownTimer,
    
    /**
     * FuelStorage để lưu trữ dữ liệu
     */
    private val fuelStorage: FuelStorage,
    
    /**
     * FuelConfig để lấy cấu hình
     */
    private val fuelConfig: FuelConfig
) : Listener {
    
    /**
     * Xử lý khi người chơi tham gia server
     */
    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        
        // Nạp dữ liệu nhiên liệu cho người chơi
        if (fuelConfig.refillOnJoin) {
            // Nếu cấu hình nạp lại khi tham gia
            fuelManager.refillFuel(player)
        } else {
            // Nếu không, tải dữ liệu đã lưu
            fuelStorage.loadPlayerData(player)
        }
    }
    
    /**
     * Xử lý khi người chơi thoát khỏi server
     */
    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        
        // Lưu dữ liệu nhiên liệu nếu cấu hình cho phép
        if (fuelConfig.saveOnQuit) {
            fuelStorage.savePlayerData(player)
        }

        // Xóa dữ liệu tạm thời
        fuelManager.removePlayer(player)
    }
    
    /**
     * Xử lý khi người chơi bật/tắt chế độ bay
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerToggleFlight(event: PlayerToggleFlightEvent) {
        val player = event.player
        val isFlying = event.isFlying
        
        // Nếu người chơi đang cố gắng bay
        if (isFlying && !player.isOp && !player.hasPermission("flyfuel.bypass")) {
            // Kiểm tra xem người chơi có đủ nhiên liệu không
            if (!fuelManager.hasFuel(player, 0.0)) {
                // Nếu không đủ nhiên liệu, hủy sự kiện
                event.isCancelled = true
                player.sendMessage("§cBạn không có đủ nhiên liệu để bay!")
                return
            }
        }
        
        player.isFlying = isFlying
    }
} 