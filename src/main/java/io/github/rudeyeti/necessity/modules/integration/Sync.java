package io.github.rudeyeti.necessity.modules.integration;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.okhttp3.ResponseBody;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sync {
    private static void getMembersFirstPage() {
        Necessity.membersFirstPage = Config.get.legacyMode ? SyncBuildersLegacy.getWebsite(1) : SyncBuilders.getWebsite();
    }

    protected static void initialize() {
        try {
            if (Config.get.integration) {
                getMembersFirstPage();
                String initialBuilders = Html.getBuilderCount(Necessity.membersFirstPage);

                getMembersFirstPage();
                Html.setLastPage(Necessity.membersFirstPage);

                if (!Config.get.globalRoleChanges) {
                    Necessity.initialBuildTeamMembersList = Config.get.legacyMode ? SyncBuildersLegacy.getWebsiteMembersList() : SyncBuilders.getWebsiteMembersList();
                }

                while (true) {
                    Thread.sleep(1000);

                    getMembersFirstPage();
                    String builders = Html.getBuilderCount(Necessity.membersFirstPage);

                    if (initialBuilders.equals(builders)) {
                        continue;
                    }

                    sync();

                    if (!Config.get.globalRoleChanges) {
                        Necessity.initialBuildTeamMembersList = Config.get.legacyMode ? SyncBuildersLegacy.getWebsiteMembersList() : SyncBuilders.getWebsiteMembersList();
                    }

                    initialBuilders = builders;

                    getMembersFirstPage();
                    Html.setLastPage(Necessity.membersFirstPage);
                }
            }
        } catch (InterruptedException error) {
            error.printStackTrace();
        }
    }
    
    protected static void join(PlayerJoinEvent event) {
        String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(event.getPlayer().getUniqueId());

        if (userId != null) {
            Member member = Necessity.guild.getMemberById(userId);
            boolean hasRole = member.getRoles().contains(Necessity.builderRole);
            boolean inGroup = Plugins.getVault().playerInGroup(event.getPlayer(), Config.get.minecraftRoleName);

            if (hasRole && !inGroup) {
                Plugins.getVault().playerAddGroup(event.getPlayer(), Config.get.minecraftRoleName);
                Roles.logRoleChange(member, "promoted to", Config.get.minecraftRoleName, false);
            } else if (!hasRole && inGroup) {
                Plugins.getVault().playerRemoveGroup(event.getPlayer(), Config.get.minecraftRoleName);
                Roles.logRoleChange(member, "demoted from", Config.get.minecraftRoleName, false);
            }
        }
    }

    protected synchronized static void sync() {
        if (Config.get.globalRoleChanges) {
            syncAllUsers();
        } else {
            syncUser();
        }
    }
    
    protected synchronized static void syncAllUsers() {
        List<Member> members = Config.get.legacyMode ? SyncBuildersLegacy.getWebsiteMembersList() : SyncBuilders.getWebsiteMembersList();
        List<Member> membersToDemote = Necessity.guild.getMembersWithRoles(Necessity.builderRole);

        for (Member member : members) {
            membersToDemote.remove(member);
            Roles.addRole(member);
        }

        for (Member member : membersToDemote) {
            Roles.removeRole(member);
        }
    }

    protected synchronized static void syncUser() {
        int initialMembersSize = Config.get.legacyMode ? SyncBuildersLegacy.membersSize : SyncBuilders.membersSize;
        List<Member> members = Config.get.legacyMode ? SyncBuildersLegacy.getWebsiteMembersList() : SyncBuilders.getWebsiteMembersList();
        int membersSize = Config.get.legacyMode ? SyncBuildersLegacy.membersSize : SyncBuilders.membersSize;

        Member member;
        if (initialMembersSize - membersSize > 0) {
            Necessity.initialBuildTeamMembersList.removeAll(members);
            member = Necessity.initialBuildTeamMembersList.get(0);
            Roles.removeRole(member);
        } else {
            members.removeAll(Necessity.initialBuildTeamMembersList);
            member = members.get(0);
            Roles.addRole(member);
        }

        Necessity.lastRoleChange = member;
    }
}
