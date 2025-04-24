package org.htilssu.flyFuel.listeners

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.htilssu.flyFuel.FuelManager
import org.htilssu.flyFuel.events.CountdownEvent

/**
 * Listener xử lý các sự kiện đếm ngược và tiêu hao nhiên liệu
 */
class CountdownListener(
    /**
     * FuelManager để quản lý nhiên liệu
     */
    private val fuelManager: FuelManager,
    
    /**
     * Tốc độ tiêu hao nhiên liệu mỗi giây
     */
    private var consumptionRate: Double = 1.0,
    
    /**
     * Ngưỡng cảnh báo nhiên liệu thấp
     */
    private var lowFuelThreshold: Double = 10.0,
    
    /**
     * Có hiển thị thanh action bar không
     */
    private var showActionBar: Boolean = true
) : Listener {
    
    /**
     * Map lưu thời điểm cảnh báo cuối cùng để tránh spam cảnh báo
     */
    private val lastWarningTime: MutableMap<String, Long> = mutableMapOf()
    
    /**
     * Xử lý khi sự kiện đếm ngược được kích hoạt
     */
    @EventHandler(priority = EventPriority.NORMAL)
    fun onCountdown(event: CountdownEvent) {
        val player = event.player
        
        // Kiểm tra xem người chơi có đang bay không
        if (event.isFlying) {
            // Kiểm tra quyền bỏ qua tiêu hao nhiên liệu
            if (player.hasPermission("flyfuel.bypass")) {
                return
            }
            
            // Tiêu hao nhiên liệu
            processPlayerFuel(player)
        }
    }
    
    /**
     * Xử lý việc tiêu hao nhiên liệu của người chơi
     * 
     * @param player Người chơi cần xử lý
     */
    private fun processPlayerFuel(player: Player) {
        // Tiêu hao nhiên liệu dựa trên tốc độ tiêu hao
        val remainingFuel = fuelManager.consumeFuel(player, consumptionRate)
        
        // Kiểm tra nhiên liệu còn lại
        if (remainingFuel <= 0) {
            // Hết nhiên liệu, dừng bay
            fuelManager.setFlying(player, false)
            player.sendMessage("${ChatColor.RED}Bạn đã hết nhiên liệu! Không thể bay tiếp.")
        } else if (remainingFuel <= lowFuelThreshold) {
            // Kiểm tra thời gian để tránh spam cảnh báo
            val playerId = player.uniqueId.toString()
            val currentTime = System.currentTimeMillis()
            val lastTime = lastWarningTime[playerId] ?: 0L
            
            // Chỉ cảnh báo mỗi 5 giây
            if (currentTime - lastTime > 5000) {
                player.sendMessage("${ChatColor.YELLOW}Cảnh báo: Nhiên liệu còn thấp (${String.format("%.1f", remainingFuel)})!")
                lastWarningTime[playerId] = currentTime
            }
        }
        
        // Hiển thị thanh hành động với lượng nhiên liệu hiện tại
        if (showActionBar) {
            updateActionBar(player, remainingFuel)
        }
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
    
    /**
     * Cập nhật cấu hình tiêu hao nhiên liệu
     * 
     * @param rate Tốc độ tiêu hao nhiên liệu mới
     */
    fun setConsumptionRate(rate: Double) {
        this.consumptionRate = rate
    }
    
    /**
     * Cập nhật ngưỡng cảnh báo nhiên liệu thấp
     * 
     * @param threshold Ngưỡng cảnh báo mới
     */
    fun setLowFuelThreshold(threshold: Double) {
        this.lowFuelThreshold = threshold
    }
    
    /**
     * Thiết lập việc hiển thị thanh action bar
     * 
     * @param show true để hiển thị, false để ẩn
     */
    fun setShowActionBar(show: Boolean) {
        this.showActionBar = show
    }
} 