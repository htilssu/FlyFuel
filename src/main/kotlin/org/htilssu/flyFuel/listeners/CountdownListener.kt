package org.htilssu.flyFuel.listeners

import net.kyori.adventure.audience.*
import net.kyori.adventure.text.*
import net.kyori.adventure.text.format.*
import org.bukkit.entity.*
import org.bukkit.event.*
import org.htilssu.flyFuel.*
import org.htilssu.flyFuel.events.*

/**
 * Listener xử lý các sự kiện đếm ngược và tiêu hao nhiên liệu
 */
class CountdownListener(
    /**
     * FuelManager để quản lý nhiên liệu
     */
    private val fuelManager: FuelManager,

    /**
     * Tốc độ tiêu hao nhiên liệu khi đi bộ thường (đơn vị/giây)
     */
    private var consumptionRate: Double = 1.0,

    /**
     * Tốc độ tiêu hao nhiên liệu khi chạy nước rút (đơn vị/giây)
     */
    private var sprintConsumptionRate: Double = 2.0,

    /**
     * Ngưỡng cảnh báo nhiên liệu thấp
     */
    private var lowFuelThreshold: Double = 20.0,

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
        if (player.hasPermission("flyfuel.bypass")) {
            return
        }

        if (!player.isFlying) return

        processPlayerFuel(player)
    }

    /**
     * Xử lý việc tiêu hao nhiên liệu của người chơi
     *
     * @param player Người chơi cần xử lý
     */
    private fun processPlayerFuel(player: Player) { // Xác định tốc độ tiêu hao dựa vào trạng thái di chuyển của người chơi
        val actualConsumptionRate = if (player.isSprinting) {
            sprintConsumptionRate
        } else {
            consumptionRate
        }

        // Tiêu hao nhiên liệu dựa trên tốc độ tiêu hao
        val remainingFuel = fuelManager.consumeFuel(player, actualConsumptionRate)

        // Kiểm tra nhiên liệu còn lại
        if (remainingFuel <= 0) { // Hết nhiên liệu, dừng bay
            fuelManager.setFlying(player, false)

            // Sử dụng Adventure API để gửi thông báo hết nhiên liệu
            player.sendMessage(
                Component.text("Bạn đã hết nhiên liệu! Không thể bay tiếp.").color(NamedTextColor.RED)
            )
        } else if (remainingFuel <= lowFuelThreshold) { // Kiểm tra thời gian để tránh spam cảnh báo
            val playerId = player.uniqueId.toString()
            val currentTime = System.currentTimeMillis()
            val lastTime = lastWarningTime[playerId] ?: 0L

            // Chỉ cảnh báo mỗi 5 giây
            if (currentTime - lastTime > 5000) { // Sử dụng Adventure API để gửi cảnh báo nhiên liệu thấp
                player.sendMessage(
                    Component.text("Cảnh báo: Nhiên liệu còn thấp (${String.format("%.1f", remainingFuel)})!")
                        .color(NamedTextColor.YELLOW)
                )
                lastWarningTime[playerId] = currentTime
            }
        }

        // Hiển thị thanh hành động với lượng nhiên liệu hiện tại
        if (showActionBar) {
            updateActionBar(player, remainingFuel)
        }
    }

    /**
     * Cập nhật thanh hành động hiển thị nhiên liệu sử dụng Adventure API
     *
     * @param player Người chơi cần cập nhật
     * @param fuel Lượng nhiên liệu hiện tại
     */
    private fun updateActionBar(player: Player, fuel: Double) { // Tính toán tỷ lệ nhiên liệu
        val fuelRatio = fuel / fuelManager.maxFuel
        val barLength = 20
        val filledLength = (fuelRatio * barLength).toInt()

        // Chọn màu dựa trên lượng nhiên liệu
        val barColor = when {
            fuelRatio > 0.5 -> NamedTextColor.GREEN
            fuelRatio > 0.25 -> NamedTextColor.YELLOW
            else -> NamedTextColor.RED
        }

        // Tạo thanh nhiên liệu với Adventure API
        val filledBar = Component.text("|".repeat(filledLength)).color(barColor)
        val emptyBar = Component.text("|".repeat(barLength - filledLength)).color(NamedTextColor.GRAY)

        // Tạo component đầy đủ với thông tin nhiên liệu
        val fuelInfoText =
            Component.text("${String.format("%.1f", fuel)}/${fuelManager.maxFuel}").color(NamedTextColor.WHITE)

        val message = Component.text("Nhiên liệu: ").color(NamedTextColor.GOLD).append(filledBar).append(emptyBar)
            .append(Component.space()).append(fuelInfoText)

        // Sử dụng Adventure API để gửi action bar message
        try { // Cast Player sang Audience để sử dụng các tính năng của Adventure API
            val audience = player as Audience
            audience.sendActionBar(message)
        } catch (e: Exception) { // Fallback nếu không thể sử dụng action bar
            player.sendMessage(
                Component.text("Nhiên liệu: ${String.format("%.1f", fuel)}/${fuelManager.maxFuel}")
                    .color(NamedTextColor.GOLD)
            )
        }
    }

    /**
     * Cập nhật cấu hình tiêu hao nhiên liệu khi đi bộ
     *
     * @param rate Tốc độ tiêu hao nhiên liệu mới
     */
    fun setConsumptionRate(rate: Double) {
        this.consumptionRate = rate
    }

    /**
     * Cập nhật cấu hình tiêu hao nhiên liệu khi chạy nước rút
     *
     * @param rate Tốc độ tiêu hao nhiên liệu khi chạy mới
     */
    fun setSprintConsumptionRate(rate: Double) {
        this.sprintConsumptionRate = rate
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