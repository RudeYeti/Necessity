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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NecessityCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            String subcommands = "List of available subcommands:\n";
            String reload = "reload - Updates any values that were modified in the configuration.";

            if (Control.isEnabled) {
                sender.sendMessage(subcommands +
                                   "info - Shows details about the author and the version of this plugin.\n" +
                                   reload + "\n" +
                                   "stats - Lists different information regarding the members.");
            } else {
                sender.sendMessage(subcommands + reload);
            }
        } else if (Control.isEnabled && args[0].matches("i(nfo)?(rmation)?|authors?|ver(sion)?")) {
            InfoSubcommand.execute(sender);
        } else if (args[0].matches("r(e?(load|start|boot))?|(en|dis)able")) {
            ReloadSubcommand.execute(sender);
        } else if (Control.isEnabled && args[0].matches("s(tat)?(istic)?s?")) {
            StatsSubcommand.execute(sender);
        } else {
            String usage = Control.isEnabled ? "Usage: /" + label + " <info | reload | stats>" : "Usage: /" + label + " <reload>";
            sender.sendMessage(ChatColor.RED + usage);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Return an empty list instead of null, so the player name does not keep appearing in the command arguments.
        if (args.length < 2) {
            List<String> list = Control.isEnabled ? Arrays.asList("info", "reload", "stats") : Collections.singletonList("reload");
            return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
        } else {
            return Collections.singletonList("");
        }
    }
}