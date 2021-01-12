package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

public class ReloadSubcommand {
    public static void execute(CommandSender sender) {
        if (sender.hasPermission("earthessentials.reload") || sender.isOp()) {
            Configuration oldConfig = Config.config;
            Necessity.plugin.reloadConfig();
            Necessity.server.reloadWhitelist();
            Config.config = Necessity.plugin.getConfig();

            if (!Config.validateConfig()) {
                Config.config = oldConfig;
                Config.updateConfig();
                sender.sendMessage("The configuration was invalid, reverting back to the previous state.");
            } else {
                Config.updateConfig();
                sender.sendMessage("The plugin has been successfully reloaded.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: You are missing the correct permission to perform this command.");
        }
    }
}