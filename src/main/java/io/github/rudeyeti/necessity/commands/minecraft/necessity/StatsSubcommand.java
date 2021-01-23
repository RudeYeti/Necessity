package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsSubcommand {
    public static void execute(CommandSender sender) {
        if (sender.hasPermission("necessity.stats") || sender.isOp()) {
            int playersWithGroup = 0;

            for (Player player : Necessity.plugin.getServer().getOnlinePlayers()) {
                if (Plugins.getVault().playerInGroup(player, Config.get.minecraftRoleName)) {
                    playersWithGroup++;
                }
            }

            sender.sendMessage("Various statistics:\n" +
                               "Discord Members - " + Necessity.guild.getMemberCount() + "\n" +
                               "Discord Builders - " + Necessity.guild.getMembersWithRoles(Necessity.builderRole).size() + "\n" +
                               "Minecraft Players - " + Necessity.plugin.getServer().getOnlinePlayers().size() + "\n" +
                               "Minecraft Builders - " + playersWithGroup);
        } else {
            sender.sendMessage(ChatColor.RED + "You are missing the correct permission to perform this command.");
        }
    }
}
