package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReloadSubcommand {
    public static void execute(CommandSender sender) {
        if (sender.hasPermission("necessity.reload") || sender.isOp()) {
            try {
                Path config = new File(Necessity.plugin.getDataFolder(), "config.yml").toPath();
                byte[] oldConfig = Files.readAllBytes(config);
                Necessity.plugin.reloadConfig();
                Necessity.server.reloadWhitelist();
                Config.config = Necessity.plugin.getConfig();

                if (!Config.validateConfig()) {
                    Files.write(config, oldConfig);
                    Config.config = Necessity.plugin.getConfig();
                    Necessity.plugin.reloadConfig();
                    Config.updateConfig();

                    sender.sendMessage("The configuration was invalid, reverting back to the previous state.");
                } else {
                    Config.updateConfig();
                    sender.sendMessage("The plugin has been successfully reloaded.");
                }
            } catch (IOException error) {
                error.printStackTrace();
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: You are missing the correct permission to perform this command.");
        }
    }
}