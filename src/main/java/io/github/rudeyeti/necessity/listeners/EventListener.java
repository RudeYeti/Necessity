package io.github.rudeyeti.necessity.listeners;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.utils.Status;
import io.github.rudeyeti.necessity.utils.integration.Integration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        try {
            Integration.join(event);
            Necessity.onlinePlayers.add(event.getPlayer().getName());
            Necessity.statusChannel.editMessageById(Config.messageId, Status.serverOn().build()).queue();
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        try {
            Necessity.onlinePlayers.remove(event.getPlayer().getName());
            Necessity.statusChannel.editMessageById(Config.messageId, Status.serverOn().build()).queue();
        } catch (NullPointerException ignored) {}
    }
}
