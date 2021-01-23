package io.github.rudeyeti.necessity.modules.activity;

import io.github.rudeyeti.necessity.modules.ModuleManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Activity {

    public static boolean isEnabled() {
        return ModuleManager.isEnabled("Activity").containsKey(true);
    }

    public static List<String> activity(String time) {
        return isEnabled() ? Generate.activity(time) : null;
    }

    public static void file(CommandSender sender, String time) {
        if (isEnabled()) {
            Generate.file(sender, time);
        }
    }
}
