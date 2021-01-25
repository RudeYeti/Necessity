package io.github.rudeyeti.necessity.commands.minecraft.necessity;

import github.scarsz.discordsrv.DiscordSRV;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.commands.minecraft.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class MigrateSubcommand {
    public static void execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("necessity.migrate") || sender.isOp()) {
            String usage = String.format("Usage: /%s %s %s", label, args[0], "<add | remove>");

            if (args.length > 1) {
                switch (args[1].toLowerCase()) {
                    case "add":
                    case "remove":
                        boolean isAdd = args[1].toLowerCase().equals("add");
                        String action = isAdd ? "Added" : "Removed";
                        String from = isAdd ? "to" : "from";
                        Map<String, UUID> linkedAccounts = DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts();
                        String player = linkedAccounts.size() > 1 ? "players" : "player";

                        linkedAccounts.forEach((id, uuid) -> {
                            OfflinePlayer offlinePlayer = Necessity.server.getOfflinePlayer(uuid);
                            offlinePlayer = Necessity.server.getOfflinePlayer(offlinePlayer.getName());

                            if (offlinePlayer.isWhitelisted() == !isAdd) {
                                offlinePlayer.setWhitelisted(isAdd);
                            }
                        });

                        Necessity.server.reloadWhitelist();
                        sender.sendMessage(String.format("%s %d %s %s the whitelist.", action, linkedAccounts.size(), player, from));
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + usage);
                }
            } else {
                sender.sendMessage(ChatColor.RED + usage);
            }
        } else {
            sender.sendMessage(ChatColor.RED + CommandManager.permission);
        }
    }
}
