package io.github.rudeyeti.necessity.listeners;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Plugins;
import io.github.rudeyeti.necessity.modules.integration.Integration;
import io.github.rudeyeti.necessity.modules.status.Status;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    public static void silentJoin(boolean action, Player player) {
        String silentJoin = "discordsrv.silentjoin";
        String silentQuit = "discordsrv.silentquit";
        boolean hasSilentJoin = Plugins.getVault().playerHas(player, silentJoin);
        boolean hasSilentQuit = Plugins.getVault().playerHas(player, silentQuit);

        if (action) {
            if (!hasSilentJoin) {
                Plugins.getVault().playerAdd(player, silentJoin);
            }

            if (!hasSilentQuit) {
                Plugins.getVault().playerAdd(player, silentQuit);
            }
        } else {
            if (hasSilentJoin) {
                Plugins.getVault().playerRemove(player, silentJoin);
            }

            if (hasSilentQuit) {
                Plugins.getVault().playerRemove(player, silentQuit);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerLoginEvent(PlayerLoginEvent event) {
        if (Config.get.maintenance) {
            boolean hasPermission = Plugins.getVault().playerHas(event.getPlayer(), "necessity.maintenance.bypass");

            if (!event.getPlayer().isOp() && !hasPermission) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ChatColor.RED + "The server is currently under maintenance. Please come back later.");
            } else {
                silentJoin(true, event.getPlayer());
            }
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        try {
            Integration.join(event);

            Status.onlinePlayers.add(event.getPlayer().getName());
            Status.statusChannel.editMessageById(Config.get.messageId, Status.serverOn().build()).queue();
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        try {
            if (Config.get.maintenance) {
                silentJoin(true, event.getPlayer());
            } else {
                silentJoin(false, event.getPlayer());
            }

            Status.onlinePlayers.remove(event.getPlayer().getName());
            Status.statusChannel.editMessageById(Config.get.messageId, Status.serverOn().build()).queue();
        } catch (NullPointerException ignored) {}
    }
}
