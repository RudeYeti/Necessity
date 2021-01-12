package io.github.rudeyeti.necessity.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.ArrayUtils;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import io.github.rudeyeti.necessity.utils.Status;
import io.github.rudeyeti.necessity.utils.integration.Integration;

public class DiscordSRVListener {
    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new JDAListener());
        Necessity.guild = DiscordSRV.getPlugin().getMainGuild();

        if (Necessity.guild == null) {
            Necessity.logger.warning("Your Discord Bot must be in a Discord Server.");
            Necessity.plugin.getServer().getPluginManager().disablePlugin(Necessity.plugin);
            return;
        }

        Necessity.builderRole = Necessity.guild.getRoleById(Config.discordRoleId);

        if (Necessity.builderRole == null) {
            Necessity.logger.warning("The role with the ID " + Config.discordRoleId + " was not found in the Discord Server " + Necessity.guild.getName() + ".");
            Necessity.plugin.getServer().getPluginManager().disablePlugin(Necessity.plugin);
            return;
        } else if (!ArrayUtils.contains(Plugins.getPermissions().getGroups(), Config.minecraftRoleName)) {
            Necessity.logger.warning("The minecraft-role-name value " + Config.minecraftRoleName + " in the configuration was not registered as a group.");
            Necessity.plugin.getServer().getPluginManager().disablePlugin(Necessity.plugin);
            return;
        }

        Status.initialMessage();
        Integration.initialRequest();
    }
}
