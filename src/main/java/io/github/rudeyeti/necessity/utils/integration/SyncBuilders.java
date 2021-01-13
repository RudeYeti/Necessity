package io.github.rudeyeti.necessity.utils.integration;

import github.scarsz.discordsrv.dependencies.jackson.core.JsonProcessingException;
import github.scarsz.discordsrv.dependencies.jackson.databind.JsonNode;
import github.scarsz.discordsrv.dependencies.jackson.databind.ObjectMapper;
import github.scarsz.discordsrv.dependencies.jackson.databind.node.ArrayNode;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncBuilders {
    public static int membersSize;

    public synchronized static Document getWebsite() {
        try {
            return Jsoup.connect("https://buildtheearth.net/api/v1/members")
                    .header("Host", "buildtheearth.net")
                    .header("Authorization", "Bearer " + Config.get.apiKey)
                    .header("Accept", "application/json")
                    .userAgent("Necessity").ignoreContentType(true).get();
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    public synchronized static ArrayNode getBuilders(Document document) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonObject = mapper.readTree(document.select("body").text());
            return (ArrayNode) jsonObject.get("members");
        } catch (JsonProcessingException error) {
            error.printStackTrace();
            return null;
        }
    }

    public synchronized static List<Member> getWebsiteMembersList() {
        List<Member> members = new ArrayList<>();
        ArrayNode arrayNode = getBuilders(getWebsite());
        membersSize = 0;

        for (int i = 0; i < arrayNode.size(); i++) {
            String id = arrayNode.get(i).get("discordId").toString().replace("\"", "");
            Member member = Necessity.guild.getMemberById(id);
            membersSize++;

            if (member != null) {
                members.add(member);
            }
        }

        return members;
    }
}
