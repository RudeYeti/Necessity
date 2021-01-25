package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.commands.minecraft.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MaintenanceSubcommand {
    public static void execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("necessity.maintenance") || sender.isOp()) {
            String usage = String.format("Usage: /%s %s %s", label, args[0], "<status | toggle>");

            if (args.length > 1) {
                switch (args[1].toLowerCase()) {
                    case "status":
                        String status = Config.get.maintenance ? "On" : "Off";
                        sender.sendMessage("Maintenance Mode: " + status);
                        break;
                    case "toggle":
                        boolean option = Config.get.maintenance;
                        String action = option ? "disabled" : "enabled";

                        Config.setValue("maintenance", option, !option);
                        Config.updateConfig();
                        sender.sendMessage("Maintenance Mode has been " + action + ".");
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + usage);
                }
            } else {
                sender.sendMessage(ChatColor.RED + usage);
            }
        } else {
            sender.sendMessage(ChatColor.RED + CommandManager.permission);
        }
    }
}
