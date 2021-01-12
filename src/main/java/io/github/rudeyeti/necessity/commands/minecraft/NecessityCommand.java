package io.github.rudeyeti.necessity.commands.minecraft;

import io.github.rudeyeti.necessity.commands.minecraft.necessity.InfoSubcommand;
import io.github.rudeyeti.necessity.commands.minecraft.necessity.ReloadSubcommand;
import io.github.rudeyeti.necessity.commands.minecraft.necessity.StatsSubcommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NecessityCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("List of available subcommands:\n" +
                               "info - Shows details about the author and the version of this plugin.\n" +
                               "reload - Updates any values that were modified in the configuration.\n");
        } else if (args[0].matches("i(nfo)?(rmation)?|authors?|ver(sion)?")) {
            InfoSubcommand.execute(sender);
        } else if (args[0].matches("r(e?(load|start|boot))?|(en|dis)able")) {
            ReloadSubcommand.execute(sender);
        } else if (args[0].matches("s(tat)?(istic)?s?")) {
            StatsSubcommand.execute(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <info | reload>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Return an empty list instead of null, so the player name does not keep appearing in the command arguments.
        return (args.length <= 1) ? StringUtil.copyPartialMatches(args[0], Arrays.asList("info", "reload"), new ArrayList<>()) : Collections.singletonList("");
    }
}