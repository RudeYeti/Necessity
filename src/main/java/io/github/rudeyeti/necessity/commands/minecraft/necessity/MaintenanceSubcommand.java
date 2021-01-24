package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import io.github.rudeyeti.necessity.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MaintenanceSubcommand {
    public static void execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("necessity.maintenance") || sender.isOp()) {
            if (args.length > 1) {
                switch (args[1].toLowerCase()) {
                    case "status":
                        String status = Config.get.maintenance ? "On" : "Off";
                        sender.sendMessage("Maintenance Status: " + status);
                        break;
                    case "toggle":
                        boolean option = Config.get.maintenance;
                        String action = option ? "disabled" : "enabled";

                        Config.setValue("maintenance", option, !option);
                        Config.updateConfig();
                        sender.sendMessage("Maintenance Mode has been " + action + ".");
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <status | toggle>");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <status | toggle>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You are missing the correct permission to perform this command.");
        }
    }
}
