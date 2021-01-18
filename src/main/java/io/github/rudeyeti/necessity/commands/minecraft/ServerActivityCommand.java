package io.github.rudeyeti.necessity.commands.minecraft;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.commands.minecraft.necessity.InfoSubcommand;
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

    public static Map<String, List<String>> subcommands = new LinkedHashMap<String, List<String>>() {{
        put("generate", Arrays.asList("Creates a log file with the current recorded activity.", "g(en(erate)?)?|l(og|ist)|(creat|mak)e"));
        put("leaderboard", Arrays.asList("Lists the top five players based on activity.", "l(ead(er(board)?)?)?|high(est)?|top"));
        put("rank", Arrays.asList("Returns the current activity level of the player.", "r(ank)?|levels?|me"));
    }};

    public static Map<String, Runnable> executor = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Config.get.activity) {
            executor.put("generate", () -> GenerateSubcommand.execute(sender, label, args));
            executor.put("leaderboard", () -> LeaderboardSubcommand.execute(sender));
            executor.put("rank", () -> RankSubcommand.execute(sender));

            if (args.length == 0) {
                sender.sendMessage(CommandManager.listSubcommands(subcommands));
            } else if (!CommandManager.executeSubcommands(args[0], subcommands, executor)) {
                sender.sendMessage(ChatColor.RED + CommandManager.usage(label, subcommands));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return CommandManager.tabComplete(args.length < 2 && Config.get.activity, args, subcommands);
    }
}