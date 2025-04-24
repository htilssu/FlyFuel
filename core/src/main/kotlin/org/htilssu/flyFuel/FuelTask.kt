package org.htilssu.flyFuel

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

/**
 * Task chạy định kỳ để xử lý việc tiêu hao nhiên liệu khi bay
 */
class FuelTask(
    private val plugin: Plugin,
    private val fuelManager: FuelManager
) : BukkitRunnable() {
    
    /**
     * Khoảng thời gian giữa các lần cập nhật (ticks)
     */
    private val updateInterval: Long = 20L // 1 giây = 20 ticks
    
    /**
     * Ngưỡng nhiên liệu thấp để cảnh báo người chơi
     */
    var lowFuelThreshold: Double = 10.0
    
    /**
     * Bắt đầu task để kiểm tra và tiêu hao nhiên liệu
     */
    fun start() {
        this.runTaskTimer(plugin, updateInterval, updateInterval)
    }
    
    /**
     * Chạy mỗi khi task được gọi
     * Kiểm tra tất cả người chơi đang bay và tiêu hao nhiên liệu tương ứng
     */
    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (fuelManager.isFlying(player)) {
                processFlyingPlayer(player)
            }
        }
    }
    
    /**
     * Xử lý người chơi đang bay
     * Tiêu hao nhiên liệu và kiểm tra nhiên liệu còn lại
     *
     * @param player Người chơi đang bay
     */
    private fun processFlyingPlayer(player: Player) {
        // Kiểm tra quyền bỏ qua việc tiêu hao nhiên liệu
        if (player.hasPermission("flyfuel.bypass")) {
            return
        }
        
        // Tiêu hao nhiên liệu
        val rate = fuelManager.defaultConsumptionRate
        val remainingFuel = fuelManager.consumeFuel(player, rate)
        
        // Kiểm tra nhiên liệu còn lại
        if (remainingFuel <= 0) {
            // Hết nhiên liệu, dừng bay
            fuelManager.setFlying(player, false)
            player.sendMessage("${ChatColor.RED}Bạn đã hết nhiên liệu! Không thể bay tiếp.")
        } else if (remainingFuel <= lowFuelThreshold) {
            // Cảnh báo nhiên liệu thấp
            player.sendMessage("${ChatColor.YELLOW}Cảnh báo: Nhiên liệu còn thấp (${String.format("%.1f", remainingFuel)})!")
        }
        
        // Hiển thị thanh hành động với lượng nhiên liệu hiện tại
        updateActionBar(player, remainingFuel)
    }
    
    /**
     * Cập nhật thanh hành động hiển thị nhiên liệu
     *
     * @param player Người chơi cần cập nhật
     * @param fuel Lượng nhiên liệu hiện tại
     */
    private fun updateActionBar(player: Player, fuel: Double) {
        // Tính toán tỷ lệ nhiên liệu
        val fuelRatio = fuel / fuelManager.maxFuel
        val barLength = 20
        val filledLength = (fuelRatio * barLength).toInt()
        
        // Tạo thanh hiển thị
        val bar = StringBuilder()
        
        // Chọn màu dựa trên lượng nhiên liệu
        val barColor = when {
            fuelRatio > 0.5 -> ChatColor.GREEN
            fuelRatio > 0.25 -> ChatColor.YELLOW
            else -> ChatColor.RED
        }
        
        // Tạo thanh nhiên liệu
        bar.append(barColor)
        repeat(filledLength) { bar.append("|") }
        bar.append(ChatColor.GRAY)
        repeat(barLength - filledLength) { bar.append("|") }
        
        // Hiển thị thanh nhiên liệu và số nhiên liệu
        val message = "${ChatColor.GOLD}Nhiên liệu: $bar ${ChatColor.WHITE}${String.format("%.1f", fuel)}/${fuelManager.maxFuel}"
        
        // Gửi thông báo đến thanh hành động của người chơi
        // Sử dụng API tương thích với nhiều phiên bản
        try {
            // Sử dụng reflection để tránh lỗi biên dịch trên các phiên bản khác nhau
            val title = Class.forName("net.md_5.bungee.api.ChatMessageType").getField("ACTION_BAR").get(null)
            val chatComponent = Class.forName("net.md_5.bungee.api.chat.TextComponent")
                .getConstructor(String::class.java)
                .newInstance(message)
            
            player.javaClass.getMethod(
                "spigot"
            ).invoke(player).javaClass.getMethod(
                "sendMessage", 
                Class.forName("net.md_5.bungee.api.ChatMessageType"),
                Class.forName("net.md_5.bungee.api.chat.BaseComponent")
            ).invoke(
                player.javaClass.getMethod("spigot").invoke(player),
                title,
                chatComponent
            )
        } catch (e: Exception) {
            // Fallback nếu không thể sử dụng action bar
            player.sendMessage(message)
        }
    }
} 