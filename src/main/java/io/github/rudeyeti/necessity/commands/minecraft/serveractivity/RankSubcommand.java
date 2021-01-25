package io.github.rudeyeti.necessity.commands.minecraft.serveractivity;

import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.modules.activity.Activity;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RankSubcommand {
    public static void execute(CommandSender sender) {
        if (sender.hasPermission("necessity.rank") || sender.isOp()) {
            if (!(sender instanceof ConsoleCommandSender)) {
                Necessity.server.getScheduler().runTaskAsynchronously(
                    Necessity.plugin,
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            String time = String.valueOf(Duration.ofDays(1).multipliedBy(30).getSeconds());

                            sender.sendMessage("Retrieving rank...");

                            List<String> lines = Activity.activity(time);
                            AtomicInteger rank = new AtomicInteger();

                            lines.forEach((line) -> {
                                rank.getAndIncrement();

                                if (line.startsWith(sender.getName())) {
                                    sender.sendMessage("Rank:\n" +
                                                       rank + ": " + line
                                    );
                                }
                            });
                        }
                    }
                );
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: This command can only be performed by a player.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: You are missing the correct permission to perform this command.");
        }
    }

}
