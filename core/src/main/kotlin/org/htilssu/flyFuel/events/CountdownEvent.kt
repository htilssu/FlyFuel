package org.htilssu.flyFuel.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Sự kiện đếm ngược được kích hoạt mỗi giây
 * Được sử dụng để xử lý việc tiêu hao nhiên liệu theo thời gian
 */
class CountdownEvent(
    /**
     * Người chơi liên quan đến sự kiện
     */
    val player: Player,

    /**
     * Thời gian còn lại (tính bằng giây)
     */
    val remainingTime: Int,

    /**
     * Cho biết người chơi có đang bay hay không
     */
    val isFlying: Boolean
) : Event() {

    companion object {
        private val HANDLERS = HandlerList()

        /**
         * Lấy danh sách các handler xử lý sự kiện
         * @return HandlerList chứa các handler
         */
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

    /**
     * Lấy danh sách các handler xử lý sự kiện
     * @return HandlerList chứa các handler
     */
    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
} 