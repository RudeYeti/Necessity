package io.github.rudeyeti.necessity.utils.integration;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Integration {
    public static void join(PlayerJoinEvent event) {
        String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(event.getPlayer().getUniqueId());

        if (userId != null) {
            Member member = Necessity.guild.getMemberById(userId);
            boolean hasRole = member.getRoles().contains(Necessity.builderRole);
            boolean inGroup = Plugins.getPermissions().playerInGroup(event.getPlayer(), Config.minecraftRoleName);

            if (hasRole && !inGroup) {
                Plugins.getPermissions().playerAddGroup(event.getPlayer(), Config.minecraftRoleName);
                SyncBuilders.logRoleChange(member, "promoted to", Config.minecraftRoleName, false);
            } else if (!hasRole && inGroup) {
                Plugins.getPermissions().playerRemoveGroup(event.getPlayer(), Config.minecraftRoleName);
                SyncBuilders.logRoleChange(member, "demoted from", Config.minecraftRoleName, false);
            }
        }
    }

    public static void initialRequest() {
        try {
            final Document[] membersFirstPage = {Jsoup.connect(Config.buildTeamMembers + "?page=1").userAgent("BTEIntegration").get()};
            final String[] initialBuilders = {membersFirstPage[0].select("small").text()};

            try {
                Necessity.lastPage = Integer.parseInt(membersFirstPage[0].select("div.pagination").select("a").last().text());
            } catch (NullPointerException error) {
                Necessity.lastPage = 1;
            }

            if (!Config.globalRoleChanges) {
                Necessity.initialBuildTeamMembersList = SyncBuilders.getWebsiteMembersList();
            }

            while (true) {
                Thread.sleep(1000);

                membersFirstPage[0] = Jsoup.connect(Config.buildTeamMembers + "?page=1").userAgent("BTEIntegration").get();
                String builders = membersFirstPage[0].select("small").text();

                if (initialBuilders[0].equals(builders)) {
                    continue;
                }

                SyncBuilders.sync();

                if (!Config.globalRoleChanges) {
                    Necessity.initialBuildTeamMembersList = SyncBuilders.getWebsiteMembersList();
                }

                initialBuilders[0] = builders;
                Necessity.lastPage = Integer.parseInt(membersFirstPage[0].select("div.pagination").select("a").last().text());
            }
        } catch (HttpStatusException ignored) {
        } catch (InterruptedException | IOException error) {
        error.printStackTrace();
        }
    }
}
