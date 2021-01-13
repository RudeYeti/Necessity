package io.github.rudeyeti.necessity.commands.minecraft.serveractivity;

import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.utils.activity.Generate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.List;

public class LeaderboardSubcommand {
    public static void execute(CommandSender sender) {
        if (sender.hasPermission("necessity.leaderboard") || sender.isOp()) {
            Necessity.server.getScheduler().runTaskAsynchronously(
                Necessity.plugin,
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        String time = String.valueOf(Duration.ofDays(1).multipliedBy(30).getSeconds());

                        sender.sendMessage("Retrieving leaderboard...");

                        List<String> lines = Generate.activity(time);

                        if (lines.size() > 4) {
                            sender.sendMessage("Leaderboard:\n" +
                                               "1: " + lines.get(0) + "\n" +
                                               "2: " + lines.get(1) + "\n" +
                                               "3: " + lines.get(2) + "\n" +
                                               "4: " + lines.get(3) + "\n" +
                                               "5: " + lines.get(4)
                            );
                        } else {
                            sender.sendMessage(ChatColor.RED + "More than four whitelisted players are required for the leaderboard.");
                        }
                    }
                }
            );
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: You are missing the correct permission to perform this command.");
        }
    }
}
