package io.github.rudeyeti.necessity.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.commands.minecraft.NecessityCommand;
import io.github.rudeyeti.necessity.commands.minecraft.ServerActivityCommand;
import io.github.rudeyeti.necessity.listeners.DiscordSRVListener;
import io.github.rudeyeti.necessity.listeners.EventListener;
import io.github.rudeyeti.necessity.listeners.JDAListener;
import io.github.rudeyeti.necessity.modules.status.Status;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;

public class Control {

    public static boolean isEnabled = true;
    public static JDAListener jdaListener = new JDAListener();

    public static void enable() {
        try {
            isEnabled = true;

            if (Config.validateConfig(true)) {
                Config.updateConfig();
            } else {
                disable(true);
                return;
            }

            if (Config.get.whitelist && !Necessity.server.hasWhitelist()) {
                Necessity.server.setWhitelist(true);
            }

            if (Config.get.status) {
                // Should almost always be none, unless the plugin has somehow been reloaded.
                Necessity.server.getOnlinePlayers().clear();
                Necessity.server.getOnlinePlayers().forEach((player) -> {
                    Status.onlinePlayers.add(player.getName());
                });
            }

            Necessity.server.getPluginCommand("necessity").setExecutor(new NecessityCommand());
            Necessity.server.getPluginCommand("serveractivity").setExecutor(new ServerActivityCommand());
            Necessity.server.getPluginManager().registerEvents(new EventListener(), Necessity.plugin);

            DiscordSRV.api.subscribe(new DiscordSRVListener());

            if (DiscordSRV.isReady) {
                Discord.discordReadyEvent();
            }
        } catch (NullPointerException ignored) {}
    }

    public static void disable(boolean logToConsole) {
        try {
            isEnabled = false;

            if (logToConsole) {
                Necessity.logger.severe(ChatColor.RED + "The plugin will be temporarily disabled until the configuration is changed.");
                Necessity.logger.severe(ChatColor.RED + "Once the values have been modified, reload the plugin with: /necessity reload");
            }

            Config.updateConfig();

            if (!Config.get.whitelist && Necessity.server.hasWhitelist()) {
                Necessity.server.setWhitelist(false);
            }

            if (!Config.get.status) {
                Status.delete();
            }

            Necessity.server.getPluginCommand("necessity").setExecutor(new NecessityCommand());

            Field commandMapField = Necessity.server.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Necessity.server);

            commandMap.getCommand("serveractivity").unregister(commandMap);
            Necessity.server.getPluginCommand("serveractivity").setExecutor(null);

            PlayerJoinEvent.getHandlerList().unregister(Necessity.plugin);
            PlayerQuitEvent.getHandlerList().unregister(Necessity.plugin);

            DiscordUtil.getJda().removeEventListener(jdaListener);
            DiscordSRV.api.unsubscribe(new DiscordSRVListener());
        } catch (NullPointerException ignored) {
        } catch (NoSuchFieldException | IllegalAccessException error) {
            error.printStackTrace();
        }
    }
}
