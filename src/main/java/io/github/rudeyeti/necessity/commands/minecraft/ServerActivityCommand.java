package io.github.rudeyeti.necessity.commands.minecraft;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.commands.minecraft.serveractivity.GenerateSubcommand;
import io.github.rudeyeti.necessity.commands.minecraft.serveractivity.LeaderboardSubcommand;
import io.github.rudeyeti.necessity.commands.minecraft.serveractivity.RankSubcommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.*;

public class ServerActivityCommand implements CommandExecutor, TabExecutor {

    public static Map<String, List<String>> subcommands = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Config.get.activity) {
            subcommands.put("generate", Arrays.asList("Creates a log file with the current recorded activity.", "g(en(erate)?)?|l(og|ist)|(creat|mak)e"));
            subcommands.put("leaderboard", Arrays.asList("Lists the top five players based on activity.", "l(ead(er(board)?)?)?|high(est)?|top"));
            subcommands.put("rank", Arrays.asList("Returns the current activity level of the player.", "r(ank)?|levels?|me"));

            if (args.length == 0) {
                sender.sendMessage(CommandManager.listSubcommands(subcommands));
            } else if (args[0].matches(subcommands.get("generate").get(1))) {
                GenerateSubcommand.execute(sender, label, args);
            } else if (args[0].matches(subcommands.get("leaderboard").get(1))) {
                LeaderboardSubcommand.execute(sender);
            } else if (args[0].matches(subcommands.get("rank").get(1))) {
                RankSubcommand.execute(sender);
            } else {
                sender.sendMessage(ChatColor.RED + CommandManager.usage(label, subcommands));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Return an empty list instead of null, so the player name does not keep appearing in the command arguments.
        return (args.length < 2 && Config.get.activity) ? StringUtil.copyPartialMatches(args[0], new ArrayList<>(subcommands.keySet()), new ArrayList<>()) : Collections.singletonList("");
    }
}