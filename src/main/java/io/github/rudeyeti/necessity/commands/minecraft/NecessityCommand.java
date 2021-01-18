package io.github.rudeyeti.necessity.commands.minecraft;

import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.commands.minecraft.necessity.InfoSubcommand;
import io.github.rudeyeti.necessity.commands.minecraft.necessity.ReloadSubcommand;
import io.github.rudeyeti.necessity.commands.minecraft.necessity.StatsSubcommand;
import io.github.rudeyeti.necessity.utils.Control;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class NecessityCommand implements CommandExecutor, TabExecutor {

    public static Map<String, List<String>> subcommands = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        subcommands.put("info", Arrays.asList("Shows details about the author and the version of this plugin.", "i(nfo)?(rmation)?|authors?|ver(sion)?"));
        subcommands.put("reload", Arrays.asList("Updates any values that were modified in the configuration.", "r(e?(load|start|boot))?|(en|dis)able"));
        subcommands.put("stats", Arrays.asList("Lists different information regarding the members.", "s(tat)?(istic)?s?"));

        if (args.length == 0) {
            if (Control.isEnabled) {
                sender.sendMessage(CommandManager.listSubcommands(subcommands));
            } else {
                Map<String, List<String>> reload = new HashMap<>();

                reload.put("reload", subcommands.get("reload"));
                sender.sendMessage(CommandManager.listSubcommands(reload));
            }
        } else if (Control.isEnabled && args[0].matches(subcommands.get("info").get(1))) {
            InfoSubcommand.execute(sender);
        } else if (args[0].matches(subcommands.get("reload").get(1))) {
            ReloadSubcommand.execute(sender);
        } else if (Control.isEnabled && args[0].matches(subcommands.get("stats").get(1))) {
            StatsSubcommand.execute(sender);
        } else {
            String usage = Control.isEnabled ? CommandManager.usage(label, subcommands) : "Usage: /" + label + " <reload>";
            sender.sendMessage(ChatColor.RED + usage);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Return an empty list instead of null, so the player name does not keep appearing in the command arguments.
        if (args.length < 2) {
            List<String> list = Control.isEnabled ? new ArrayList<>(subcommands.keySet()) : Collections.singletonList("reload");
            return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
        } else {
            return Collections.singletonList("");
        }
    }
}