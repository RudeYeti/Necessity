package io.github.rudeyeti.necessity.utils.integration;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncBuilders {
    private static int membersSize;

    public synchronized static List<Member> getWebsiteMembersList() {
        List<Member> members = new ArrayList<>();
        membersSize = 0;

        try {
            for (int i = 1; i < Necessity.lastPage + 1; i++) {
                Document membersPage = Jsoup.connect(Config.buildTeamMembers + "?page=" + i).userAgent("Necessity").get();
                Elements td = membersPage.select("td");

                for (int a = 1; a < td.size(); a += 3) {
                    String username = td.get(a).text();
                    Member member = Necessity.guild.getMemberByTag(username);
                    membersSize++;

                    if (member == null) {
                        continue;
                    }

                    members.add(member);
                }
            }
        } catch (HttpStatusException ignored) {
        } catch (IOException error) {
            error.printStackTrace();
        }

        return members;
    }

    public synchronized static void logRoleChange(Member member, String roleChange, String roleName, boolean inGroup) {
        if (Config.logRoleChanges) {
            String message = "The user " + member.getUser().getAsTag() + " was " + roleChange + " " + roleName;
            if (inGroup) {
                Necessity.logger.info(message + " and " + Config.minecraftRoleName + ".");
            } else {
                Necessity.logger.info(message + ".");
            }
        }
    }

    public synchronized static void addRole(Member member) {
        Player player = Bukkit.getPlayer(DiscordSRV.getPlugin().getAccountLinkManager().getUuid(member.getId()));
        boolean hasRole = member.getRoles().contains(Necessity.builderRole);
        boolean inGroup = player != null && !Plugins.getPermissions().playerInGroup(player, Config.minecraftRoleName);

        if (!hasRole) {
            Necessity.guild.addRoleToMember(member, Necessity.builderRole).queue();

            if (inGroup) {
                Plugins.getPermissions().playerAddGroup(player, Config.minecraftRoleName);
            }

            logRoleChange(member, "promoted to", Necessity.builderRole.getName(), inGroup);
        }
    }

    public synchronized static void removeRole(Member member) {
        Player player = Bukkit.getPlayer(DiscordSRV.getPlugin().getAccountLinkManager().getUuid(member.getId()));
        boolean inGroup = player != null && Plugins.getPermissions().playerInGroup(player, Config.minecraftRoleName);
        Necessity.guild.removeRoleFromMember(member, Necessity.builderRole).queue();

        if (inGroup) {
            Plugins.getPermissions().playerRemoveGroup(player, Config.minecraftRoleName);
        }

        logRoleChange(member, "demoted from", Necessity.builderRole.getName(), inGroup);
    }

    public synchronized static void syncUser() {
        int initialMembersSize = membersSize;
        List<Member> members = getWebsiteMembersList();

        Member member;
        if (initialMembersSize - membersSize > 0) {
            Necessity.initialBuildTeamMembersList.removeAll(members);
            member = Necessity.initialBuildTeamMembersList.get(0);
            removeRole(member);
        } else {
            members.removeAll(Necessity.initialBuildTeamMembersList);
            member = members.get(0);
            addRole(member);
        }

        Necessity.lastRoleChange = member;
    }

    public synchronized static void syncAllUsers() {
        List<Member> members = getWebsiteMembersList();
        List<Member> membersToDemote = Necessity.guild.getMembersWithRoles(Necessity.builderRole);

        for (Member member : members) {
            membersToDemote.remove(member);
            addRole(member);
        }

        for (Member member : membersToDemote) {
            removeRole(member);
        }
    }

    public synchronized static void sync() {
        if (Config.globalRoleChanges) {
            syncAllUsers();
        } else {
            syncUser();
        }
    }
}
