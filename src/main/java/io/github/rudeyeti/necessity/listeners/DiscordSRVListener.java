package io.github.rudeyeti.necessity.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.ArrayUtils;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import io.github.rudeyeti.necessity.modules.integration.Integration;
import io.github.rudeyeti.necessity.modules.status.Status;
import io.github.rudeyeti.necessity.utils.Control;

public class DiscordSRVListener {
    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new JDAListener());
        Necessity.guild = DiscordSRV.getPlugin().getMainGuild();

        if (Control.isEnabled && Necessity.guild == null) {
            Necessity.logger.severe("Your Discord Bot must be in a Discord Server.");
            Control.disable();
            return;
        }

        Necessity.builderRole = Necessity.guild.getRoleById(Config.get.discordRoleId);

        if (Control.isEnabled && Necessity.builderRole == null) {
            Necessity.logger.severe("The role with the ID " + Config.get.discordRoleId + " was not found in the Discord Server " + Necessity.guild.getName() + ".");
            Control.disable();
            return;
        } else if (Control.isEnabled && !ArrayUtils.contains(Plugins.getVault().getGroups(), Config.get.minecraftRoleName)) {
            Necessity.logger.severe("The minecraft-role-name value " + Config.get.minecraftRoleName + " in the configuration was not registered as a group.");
            Control.disable();
            return;
        }

        Status.initialize();
        Integration.initialize();
    }
}
