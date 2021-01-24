package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.commands.minecraft.CommandManager;
import io.github.rudeyeti.necessity.utils.Control;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReloadSubcommand {
    public static void execute(CommandSender sender) {
        if (sender.hasPermission("necessity.reload") || sender.isOp()) {
            Path config = new File(Necessity.plugin.getDataFolder(), "config.yml").toPath();
            Map<String, Object> oldConfig = Config.getValues(true);
            Necessity.plugin.reloadConfig();
            Necessity.server.reloadWhitelist();
            Config.config = Necessity.plugin.getConfig();

            if (!Config.validateConfig(false)) {
                InputStream initialConfig = Necessity.plugin.getResource("config.yml");
                Stream<String> lines = new BufferedReader(new InputStreamReader(initialConfig)).lines();
                final String[] content = {lines.collect(Collectors.joining("\n"))};

                oldConfig.forEach((option, oldValue) -> {
                    Object newValue = Config.config.getDefaults().get(option);
                    content[0] = Config.setValue(config, content[0], option, newValue, oldValue);
                });

                Config.updateConfig();

                if (Control.isEnabled) {
                    sender.sendMessage(ChatColor.RED + "Usage: The configuration is invalid. Reverting back to the previous state.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: The configuration is still invalid. It needs to be modified before starting the plugin.");
                }
            } else {
                if (Control.isEnabled) {
                    Control.disable(false);
                    Control.enable();
                    sender.sendMessage("The plugin has been successfully reloaded.");
                } else {
                    Control.enable();
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + CommandManager.permission);
        }
    }
}