package org.htilssu.flyFuel.command

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.htilssu.flyFuel.FlyFuel

/**
 * Main command handler for the FlyFuel plugin
 */
class FlyFuelCommand(private val plugin: FlyFuel) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return showHelp(sender)
        }
        
        when (args[0].lowercase()) {
            "help" -> showHelp(sender)
            "toggle" -> toggleFlight(sender)
            "status" -> showStatus(sender)
            "refill" -> refillFuel(sender, args)
            "reload" -> reloadConfig(sender)
            "give" -> giveFuel(sender, args)
            "set" -> setFuel(sender, args)
            else -> {
                sender.sendMessage("${ChatColor.RED}Unknown command. Use /ff help to see available commands.")
                return false
            }
        }
        
        return true
    }
    
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            val completions = mutableListOf("help", "toggle", "status", "refill", "reload", "give", "set")
            return completions.filter { it.startsWith(args[0].lowercase()) }
        }
        
        if (args.size == 2 && (args[0].equals("give", ignoreCase = true) || args[0].equals("set", ignoreCase = true) || args[0].equals("refill", ignoreCase = true))) {
            return plugin.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[1], ignoreCase = true) }
        }
        
        return null
    }
    
    private fun showHelp(sender: CommandSender): Boolean {
        sender.sendMessage("${ChatColor.GOLD}==== FlyFuel Commands ====")
        sender.sendMessage("${ChatColor.YELLOW}/ff help ${ChatColor.WHITE}- Shows this help menu")
        sender.sendMessage("${ChatColor.YELLOW}/ff toggle ${ChatColor.WHITE}- Toggles flight mode on/off")
        sender.sendMessage("${ChatColor.YELLOW}/ff status ${ChatColor.WHITE}- Shows your current fuel and flying status")
        
        if (sender.hasPermission("flyfuel.admin")) {
            sender.sendMessage("${ChatColor.GOLD}==== Admin Commands ====")
            sender.sendMessage("${ChatColor.YELLOW}/ff refill [player] ${ChatColor.WHITE}- Refills fuel for yourself or another player")
            sender.sendMessage("${ChatColor.YELLOW}/ff reload ${ChatColor.WHITE}- Reloads the plugin configuration")
            sender.sendMessage("${ChatColor.YELLOW}/ff give <player> <amount> ${ChatColor.WHITE}- Gives fuel to a player")
            sender.sendMessage("${ChatColor.YELLOW}/ff set <player> <amount> ${ChatColor.WHITE}- Sets a player's fuel to a specific amount")
        }
        
        return true
    }
    
    private fun toggleFlight(sender: CommandSender): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}This command can only be used by players.")
            return false
        }
        
        if (!sender.hasPermission("flyfuel.use")) {
            sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
            return false
        }
        
        val fuelManager = plugin.fuelManager

        // Check if player has fuel to fly
        if (!sender.allowFlight && !sender.hasPermission("flyfuel.bypass") && !fuelManager.hasFuel(sender, 0.0)) {
            sender.sendMessage("${ChatColor.RED}You don't have enough fuel to fly!")
            return false
        }

        val isPlayerCanFly = sender.allowFlight

        // Toggle flying state
        if (!isPlayerCanFly) {
            fuelManager.setFlying(sender, true)
        } else {
            fuelManager.setFlying(sender, false)
        }

        sender.sendMessage(
            if (!isPlayerCanFly) "${ChatColor.GREEN}Flight mode enabled! Fuel: ${String.format("%.1f", fuelManager.getFuel(sender))}"
            else "${ChatColor.YELLOW}Flight mode disabled."
        )
        
        return true
    }
    
    private fun showStatus(sender: CommandSender): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}This command can only be used by players.")
            return false
        }
        
        val fuelManager = plugin.fuelManager
        val fuelConfig = plugin.fuelConfig
        
        val isFlying = sender.isFlying
        val currentFuel = fuelManager.getFuel(sender)
        val maxFuel = fuelConfig.maxFuel
        
        sender.sendMessage("${ChatColor.GOLD}=== Your Flying Status ===")
        sender.sendMessage(
            if (isFlying) "${ChatColor.GREEN}You are currently flying."
            else "${ChatColor.YELLOW}You are not currently flying."
        )
        sender.sendMessage("${ChatColor.GOLD}Fuel: ${ChatColor.WHITE}${String.format("%.1f", currentFuel)} / ${maxFuel}")
        
        if (sender.hasPermission("flyfuel.bypass")) {
            sender.sendMessage("${ChatColor.GREEN}You have unlimited fuel (bypass permission).")
        }
        
        return true
    }
    
    private fun refillFuel(sender: CommandSender, args: Array<out String>): Boolean {
        if (!sender.hasPermission("flyfuel.admin")) {
            sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
            return false
        }
        
        val fuelManager = plugin.fuelManager
        
        // If player is specified
        if (args.size > 1) {
            val targetPlayerName = args[1]
            val targetPlayer = plugin.server.getPlayer(targetPlayerName)
            
            if (targetPlayer == null) {
                sender.sendMessage("${ChatColor.RED}Player not found: $targetPlayerName")
                return false
            }
            
            fuelManager.refillFuel(targetPlayer)
            sender.sendMessage("${ChatColor.GREEN}Refilled fuel for ${targetPlayer.name}.")
            targetPlayer.sendMessage("${ChatColor.GREEN}Your fuel has been refilled by ${sender.name}.")
            return true
        }
        
        // If no player specified and sender is a player
        if (sender is Player) {
            fuelManager.refillFuel(sender)
            sender.sendMessage("${ChatColor.GREEN}Your fuel has been refilled.")
            return true
        }
        
        sender.sendMessage("${ChatColor.RED}Please specify a player: /ff refill <player>")
        return false
    }
    
    private fun reloadConfig(sender: CommandSender): Boolean {
        if (!sender.hasPermission("flyfuel.admin")) {
            sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
            return false
        }
        
        plugin.reloadFuelConfig()
        sender.sendMessage("${ChatColor.GREEN}FlyFuel configuration reloaded.")
        return true
    }
    
    private fun giveFuel(sender: CommandSender, args: Array<out String>): Boolean {
        if (!sender.hasPermission("flyfuel.admin")) {
            sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
            return false
        }
        
        if (args.size < 3) {
            sender.sendMessage("${ChatColor.RED}Usage: /ff give <player> <amount>")
            return false
        }
        
        val targetPlayerName = args[1]
        val targetPlayer = plugin.server.getPlayer(targetPlayerName)
        
        if (targetPlayer == null) {
            sender.sendMessage("${ChatColor.RED}Player not found: $targetPlayerName")
            return false
        }
        
        val amount = try {
            args[2].toDouble()
        } catch (e: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED}Invalid amount: ${args[2]}. Must be a number.")
            return false
        }
        
        val fuelManager = plugin.fuelManager
        val newFuel = fuelManager.addFuel(targetPlayer, amount)
        
        sender.sendMessage("${ChatColor.GREEN}Added $amount fuel to ${targetPlayer.name}. New total: $newFuel")
        targetPlayer.sendMessage("${ChatColor.GREEN}You received $amount fuel from ${sender.name}.")
        
        return true
    }
    
    private fun setFuel(sender: CommandSender, args: Array<out String>): Boolean {
        if (!sender.hasPermission("flyfuel.admin")) {
            sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
            return false
        }
        
        if (args.size < 3) {
            sender.sendMessage("${ChatColor.RED}Usage: /ff set <player> <amount>")
            return false
        }
        
        val targetPlayerName = args[1]
        val targetPlayer = plugin.server.getPlayer(targetPlayerName)
        
        if (targetPlayer == null) {
            sender.sendMessage("${ChatColor.RED}Player not found: $targetPlayerName")
            return false
        }
        
        val amount = try {
            args[2].toDouble()
        } catch (e: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED}Invalid amount: ${args[2]}. Must be a number.")
            return false
        }
        
        val fuelManager = plugin.fuelManager
        fuelManager.setFuel(targetPlayer, amount)
        
        sender.sendMessage("${ChatColor.GREEN}Set ${targetPlayer.name}'s fuel to $amount")
        targetPlayer.sendMessage("${ChatColor.GREEN}Your fuel was set to $amount by ${sender.name}.")
        
        return true
    }
}