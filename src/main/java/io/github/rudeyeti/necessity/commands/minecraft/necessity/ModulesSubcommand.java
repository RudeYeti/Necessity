package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.commands.minecraft.CommandManager;
import io.github.rudeyeti.necessity.modules.ModuleManager;
import io.github.rudeyeti.necessity.utils.Control;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ModulesSubcommand {
    public static void execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("necessity.modules") || sender.isOp()) {
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("list")) {
                    StringBuilder modules = new StringBuilder().append("List of modules:\n");

                    ModuleManager.modules.forEach((module, dependency) -> {
                        modules.append(module).append("\n");
                    });

                    sender.sendMessage(CommandManager.removeLast(modules.toString(), "\n"));
                } else if (args[1].equalsIgnoreCase("toggle")) {
                    if (args.length > 2) {
                        if (ModuleManager.modules.containsKey(StringUtils.capitalize(args[2]))) {
                            boolean option = Config.config.getBoolean(args[2].toLowerCase());
                            String action = option ? "disabled" : "enabled";

                            Config.setValue(args[2].toLowerCase(), option, !option);
                            Control.disable(false);
                            Control.enable();
                            sender.sendMessage("The module has been " + action + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: The module " + args[2] + " has not been found.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " " + args[1] + " <module>");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <list | toggle> [module]");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <list | toggle> [module]");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You are missing the correct permission to perform this command.");
        }
    }
}
