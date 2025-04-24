package org.htilssu.flyFuel

import org.bukkit.*
import org.bukkit.scheduler.*
import org.htilssu.flyFuel.events.*

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
     * Khởi động bộ đếm thời gian chung cho cả server
     * Chỉ nên gọi một lần khi server khởi động
     */
    fun start() { // Hủy task cũ nếu còn đang chạy
        stop()

        // Tạo task mới để chạy mỗi giây (20 ticks)
        mainTask = object : BukkitRunnable() {
            override fun run() { // Xử lý tất cả người chơi đang online
                for (uuid in plugin.fuelManager.flyingPlayers) {
                     val player  = Bukkit.getPlayer(uuid)
                    if (player == null || !player.isOnline) {
                        continue
                    }

                    Bukkit.getPluginManager().callEvent(CountdownEvent(player))
                }
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
    }
}