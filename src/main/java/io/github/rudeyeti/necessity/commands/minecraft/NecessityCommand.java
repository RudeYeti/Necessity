package io.github.rudeyeti.necessity.commands.minecraft;

import io.github.rudeyeti.necessity.commands.minecraft.necessity.InfoSubcommand;
import io.github.rudeyeti.necessity.commands.minecraft.necessity.ModulesSubcommand;
import io.github.rudeyeti.necessity.commands.minecraft.necessity.ReloadSubcommand;
import io.github.rudeyeti.necessity.commands.minecraft.necessity.StatsSubcommand;
import io.github.rudeyeti.necessity.utils.Control;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;

public class NecessityCommand implements CommandExecutor, TabExecutor {

    public static Map<String, List<String>> subcommands = new LinkedHashMap<String, List<String>>() {{
        put("info", Arrays.asList("Shows details about the author and the version of this plugin.", "i(nfo)?(rmation)?|authors?|ver(sion)?"));
        put("modules", Arrays.asList("Lists the various modules and enables or disables them.", "m(odules?)?"));
        put("reload", Arrays.asList("Updates any values that were modified in the configuration.", "r(e?(load|start|boot))?|(en|dis)able"));
        put("stats", Arrays.asList("Lists different information regarding the members.", "s(tat)?(istic)?s?"));
    }};

    public static Map<String, Runnable> executor = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        executor.put("info", () -> InfoSubcommand.execute(sender));
        executor.put("modules", () -> ModulesSubcommand.execute(sender, label, args));
        executor.put("reload", () -> ReloadSubcommand.execute(sender));
        executor.put("stats", () -> StatsSubcommand.execute(sender));

        if (Control.isEnabled) {
            if (args.length == 0) {
                sender.sendMessage(CommandManager.listSubcommands(subcommands));
            } else if (!CommandManager.executeSubcommands(args[0], subcommands, executor)) {
                sender.sendMessage(ChatColor.RED + CommandManager.usage(label, subcommands));
            }
        } else {
            Map<String, List<String>> reloadSubcommand = Collections.singletonMap("reload", subcommands.get("reload"));

            if (args.length == 0) {
                sender.sendMessage(CommandManager.listSubcommands(reloadSubcommand));
            } else if (!CommandManager.executeSubcommands(args[0], subcommands, Collections.singletonMap("reload", executor.get("reload")))) {
                sender.sendMessage(ChatColor.RED + CommandManager.usage(label, reloadSubcommand));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (Control.isEnabled) {
            return CommandManager.tabComplete(args.length < 2, args, subcommands);
        } else {
            return Collections.singletonList("reload");
        }
    }
}