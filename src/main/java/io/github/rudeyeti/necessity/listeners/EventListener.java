package io.github.rudeyeti.necessity.listeners;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.modules.integration.Integration;
import io.github.rudeyeti.necessity.modules.status.Status;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
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
            Status.onlinePlayers.remove(event.getPlayer().getName());
            Status.statusChannel.editMessageById(Config.get.messageId, Status.serverOn().build()).queue();
        } catch (NullPointerException ignored) {}
    }
}
