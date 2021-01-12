package io.github.rudeyeti.necessity.commands.minecraft.serveractivity;

import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.utils.activity.Generate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class GenerateSubcommand {
    public static void execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("serveractivity.generate") || sender.isOp()) {
            if (args.length > 1) {
                Necessity.server.getScheduler().runTaskAsynchronously(
                    Necessity.plugin,
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Generate.file(sender, args[1]);
                        }
                    }
                );
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <time>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: You are missing the correct permission to perform this command.");
        }
    }
}
