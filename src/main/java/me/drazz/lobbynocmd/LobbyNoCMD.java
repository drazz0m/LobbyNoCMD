package me.drazz.lobbynocmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class LobbyNoCMD extends JavaPlugin implements Listener, CommandExecutor {
    private List<String> allowedCommands;
    private boolean messageEnabled;
    private String denied_message;

    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        allowedCommands = getConfig().getStringList("allowed_commands");
        messageEnabled = getConfig().getBoolean("message_enabled");
        denied_message = getConfig().getString("denied_command_message");

        Objects.requireNonNull(getCommand("lobbynocmd")).setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void onDisable() {
        HandlerList.unregisterAll();
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasPermission("lobbynocmd.bypass")) {
            return;
        }

        String[] args = e.getMessage().split(" ");
        String command = args[0].substring(1);
        if (!allowedCommands.contains(command)) {
            e.setCancelled(true);
            if (messageEnabled) {
                e.getPlayer().sendMessage(denied_message);
            }
        }
    }

    @EventHandler
    public void onCommandTabComplete(final TabCompleteEvent event) {
        if (event.getSender().hasPermission("lobbynocmd.bypass")) {
            return;
        }
        event.setCancelled(true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sender.sendMessage("§8/§7lnc §6reload §7- Reload plugin");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload"))
            if (sender.hasPermission("lobbynocmd.reload")) {
                reloadConfig();

                allowedCommands = getConfig().getStringList("allowed_commands");
                messageEnabled = getConfig().getBoolean("message_enabled");
                denied_message = getConfig().getString("denied_command_message");

                sender.sendMessage("§aSuccessfully reloaded");
            }
            else {
                sender.sendMessage("§cNo permission!");
            }
        else {
            sender.sendMessage("§8/§7lnc §6reload §7- Reload plugin");
        }
        return true;
    }
}
