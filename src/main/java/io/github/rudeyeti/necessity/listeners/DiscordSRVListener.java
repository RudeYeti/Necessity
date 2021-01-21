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
        try {
            DiscordUtil.getJda().addEventListener(new JDAListener());
            Necessity.guild = DiscordSRV.getPlugin().getMainGuild();
            Necessity.builderRole = Necessity.guild.getRoleById(Config.get.discordRoleId);

            if (Control.isEnabled) {
                if (Necessity.builderRole == null) {
                    Necessity.logger.severe(String.format(Config.getMessage, "discord-role-id", "a real role."));
                    Control.disable(true);
                    return;
                } else if (!ArrayUtils.contains(Plugins.getVault().getGroups(), Config.get.minecraftRoleName)) {
                    Necessity.logger.severe(String.format(Config.getMessage, "minecraft-role-name", "a real group."));
                    Control.disable(true);
                    return;
                }
            }

            Status.initialize();
            Integration.initialize();
        } catch (NullPointerException error) {
            if (Control.isEnabled && Necessity.guild == null) {
                Necessity.logger.severe("The Discord Bot must be in a Discord Server.");
                Control.disable(true);
            }
        }
    }
}
