package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import io.github.rudeyeti.necessity.Necessity;
import org.bukkit.command.CommandSender;

public class InfoSubcommand {
    public static void execute(CommandSender sender) {
        sender.sendMessage("General information:\n" +
                "Author - " + Necessity.plugin.getDescription().getAuthors().get(0) + "\n" +
                "Version - " + Necessity.plugin.getDescription().getVersion());
    }
}
