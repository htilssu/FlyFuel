package org.htilssu.flyFuel

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.htilssu.flyFuel.events.CountdownEvent

/**
 * Timer đếm ngược và kích hoạt CountdownEvent theo thời gian
 * Sử dụng một timer duy nhất cho toàn bộ server
 */
class CountdownTimer(private val plugin: Plugin) {
    
    /**
     * Task chính của timer
     */
    private var mainTask: BukkitTask? = null

    /**
     * Danh sách người chơi đang được theo dõi
     */
    private val activePlayers: MutableSet<String> = mutableSetOf()
    
    /**
     * Khởi động bộ đếm thời gian chung cho cả server
     * Chỉ nên gọi một lần khi server khởi động
     */
    fun start() {
        // Hủy task cũ nếu còn đang chạy
        stop()
        
        // Tạo task mới để chạy mỗi giây (20 ticks)
        mainTask = object : BukkitRunnable() {
            override fun run() {
                // Xử lý tất cả người chơi đang online
                Bukkit.getOnlinePlayers().forEach { player ->
                    // Kích hoạt CountdownEvent để listener có thể xử lý
                    if (isTracking(player)) {
                        val isFlying = player.isFlying
                        val countdownEvent = CountdownEvent(player, 0, isFlying)
                        Bukkit.getPluginManager().callEvent(countdownEvent)
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 20 ticks = 1 giây
        
        plugin.logger.info("CountdownTimer đã khởi động cho toàn server")
    }
    
    /**
     * Dừng bộ đếm thời gian
     * Nên được gọi khi server tắt
     */
    fun stop() {
        mainTask?.cancel()
        mainTask = null
        activePlayers.clear()
    }
    
    /**
     * Thêm người chơi vào danh sách theo dõi
     * 
     * @param player Người chơi cần theo dõi
     */
    fun trackPlayer(player: Player) {
        activePlayers.add(player.uniqueId.toString())
    }
    
    /**
     * Xóa người chơi khỏi danh sách theo dõi
     * 
     * @param player Người chơi cần xóa
     */
    fun untrackPlayer(player: Player) {
        activePlayers.remove(player.uniqueId.toString())
    }
    
    /**
     * Kiểm tra xem người chơi có đang được theo dõi không
     * 
     * @param player Người chơi cần kiểm tra
     * @return true nếu đang được theo dõi, false nếu không
     */
    fun isTracking(player: Player): Boolean {
        return activePlayers.contains(player.uniqueId.toString())
    }
    
    /**
     * Xóa tất cả người chơi khỏi danh sách theo dõi
     */
    fun untrackAllPlayers() {
        activePlayers.clear()
    }
} 