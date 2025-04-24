package org.htilssu.flyFuel

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Quản lý nhiên liệu của người chơi
 * Lưu trữ và theo dõi lượng nhiên liệu còn lại của từng người chơi
 */
class FuelManager {
    /**
     * Map lưu trữ lượng nhiên liệu của người chơi theo UUID
     */
    private val playerFuel: MutableMap<UUID, Double> = ConcurrentHashMap()
    
    /**
     * Map lưu trữ thông tin người chơi có đang bay hay không
     */
    private val playerFlying: MutableMap<UUID, Boolean> = ConcurrentHashMap()
    
    /**
     * Tốc độ tiêu hao nhiên liệu mặc định (nhiên liệu/giây)
     */
    var defaultConsumptionRate: Double = 1.0
    
    /**
     * Tốc độ tiêu hao nhiên liệu khi chạy nước rút (đơn vị/giây)
     */
    var sprintConsumptionRate: Double = 2.0
    
    /**
     * Mức nhiên liệu mặc định khi người chơi tham gia lần đầu
     */
    var defaultFuel: Double = 50.0
    
    /**
     * Mức nhiên liệu tối đa cho mỗi người chơi
     */
    var maxFuel: Double = 100.0
    
    /**
     * Tự động tắt bay khi hết nhiên liệu
     */
    var autoDisableFlight: Boolean = true
    
    /**
     * Tự động kích hoạt bay khi nạp nhiên liệu (nếu đủ quyền)
     */
    var autoEnableFlight: Boolean = true
    
    /**
     * Kiểm tra xem người chơi có đủ nhiên liệu không
     * 
     * @param player Người chơi cần kiểm tra
     * @param amount Lượng nhiên liệu cần kiểm tra
     * @return true nếu người chơi có đủ nhiên liệu, false nếu không
     */
    fun hasFuel(player: Player, amount: Double = 0.0): Boolean {
        return getFuel(player) > amount
    }
    
    /**
     * Lấy lượng nhiên liệu hiện tại của người chơi
     * 
     * @param player Người chơi cần lấy thông tin
     * @return Lượng nhiên liệu hiện tại
     */
    fun getFuel(player: Player): Double {
        return playerFuel.getOrDefault(player.uniqueId, defaultFuel)
    }
    
    /**
     * Thiết lập lượng nhiên liệu cho người chơi
     * 
     * @param player Người chơi cần thiết lập
     * @param amount Lượng nhiên liệu mới
     */
    fun setFuel(player: Player, amount: Double) {
        val cappedAmount = amount.coerceIn(0.0, maxFuel)
        playerFuel[player.uniqueId] = cappedAmount
        
        // Nếu hết nhiên liệu và autoDisableFlight được bật, tắt chế độ bay
        if (cappedAmount <= 0 && autoDisableFlight && isFlying(player)) {
            setFlying(player, false)
        }
        
        // Nếu đang có nhiên liệu và autoEnableFlight được bật, bật chế độ bay
        if (cappedAmount > 0 && autoEnableFlight && player.allowFlight && !isFlying(player)) {
            setFlying(player, true)
        }
    }
    
    /**
     * Thêm nhiên liệu cho người chơi
     * 
     * @param player Người chơi cần thêm nhiên liệu
     * @param amount Lượng nhiên liệu thêm vào
     * @return Lượng nhiên liệu mới
     */
    fun addFuel(player: Player, amount: Double): Double {
        val currentFuel = getFuel(player)
        val newFuel = (currentFuel + amount).coerceIn(0.0, maxFuel)
        setFuel(player, newFuel)
        return newFuel
    }
    
    /**
     * Tiêu thụ nhiên liệu của người chơi
     * 
     * @param player Người chơi cần trừ nhiên liệu
     * @param amount Lượng nhiên liệu cần tiêu thụ
     * @return Lượng nhiên liệu còn lại
     */
    fun consumeFuel(player: Player, amount: Double): Double {
        return addFuel(player, -amount)
    }
    
    /**
     * Đặt trạng thái bay của người chơi
     * 
     * @param player Người chơi cần đặt trạng thái
     * @param flying true nếu đang bay, false nếu không
     */
    fun setFlying(player: Player, flying: Boolean) {
        playerFlying[player.uniqueId] = flying
        // Đặt trạng thái bay của người chơi trong Bukkit
        if (player.allowFlight) {
            player.isFlying = flying
        }
    }
    
    /**
     * Kiểm tra xem người chơi có đang bay không
     * 
     * @param player Người chơi cần kiểm tra
     * @return true nếu đang bay, false nếu không
     */
    fun isFlying(player: Player): Boolean {
        return playerFlying.getOrDefault(player.uniqueId, false)
    }
    
    /**
     * Nạp đầy nhiên liệu cho người chơi
     * 
     * @param player Người chơi cần nạp đầy
     */
    fun refillFuel(player: Player) {
        setFuel(player, maxFuel)
    }
    
    /**
     * Xóa dữ liệu của người chơi khi họ thoát khỏi server
     * 
     * @param player Người chơi cần xóa dữ liệu
     */
    fun removePlayer(player: Player) {
        playerFuel.remove(player.uniqueId)
        playerFlying.remove(player.uniqueId)
    }
    
    /**
     * Khởi tạo người chơi mới với nhiên liệu mặc định
     * 
     * @param player Người chơi cần khởi tạo
     */
    fun initializePlayer(player: Player) {
        if (!playerFuel.containsKey(player.uniqueId)) {
            setFuel(player, defaultFuel)
        }
    }
} 