package org.htilssu.flyFuel

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.htilssu.flyFuel.events.CountdownEvent

/**
 * Timer đếm ngược và kích hoạt CountdownEvent theo thời gian
 * Sử dụng một timer duy nhất cho toàn bộ server
 */
class CountdownTimer(private val plugin: FlyFuel) {

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
                plugin.fuelManager.flyingPlayers.forEach { playerUUID -> {
                    val player = plugin.server.getPlayer(playerUUID)
                    if (player != null) {
                        Bukkit.getPluginManager().callEvent(CountdownEvent(player))
                    }
                } }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 20 ticks = 1 giây
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
}