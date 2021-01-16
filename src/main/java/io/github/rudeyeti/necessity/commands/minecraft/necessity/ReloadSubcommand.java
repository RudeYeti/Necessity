package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ReloadSubcommand {
    private static Object addQuotes(Object object) {
        if (object instanceof String && !((String) object).contains("\"")) {
            return "\"" + object + "\"";
        } else {
            return object;
        }
    }

    public static void execute(CommandSender sender) {
        if (sender.hasPermission("necessity.reload") || sender.isOp()) {
            try {
                Path config = new File(Necessity.plugin.getDataFolder(), "config.yml").toPath();
                Map<String, Object> oldConfig = Config.getValues(true);
                Necessity.plugin.reloadConfig();
                Necessity.server.reloadWhitelist();
                Config.config = Necessity.plugin.getConfig();

                if (!Config.validateConfig(false)) {
                    final String[] content = {new String(Files.readAllBytes(config))};
                    Map<String, Object> newConfig = Config.getValues(false);

                    newConfig.forEach((option, newValue) -> {
                        String key = option + ": ";
                        newValue = addQuotes(newValue);
                        Object oldValue = addQuotes(oldConfig.get(option));

                        content[0] = content[0].replaceAll(key + newValue, key + oldValue);
                    });

                    Files.write(config, content[0].getBytes());
                    Necessity.plugin.reloadConfig();
                    Config.updateConfig();

                    sender.sendMessage(ChatColor.RED + "Usage: The configuration was invalid. Reverting back to the previous state.");
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