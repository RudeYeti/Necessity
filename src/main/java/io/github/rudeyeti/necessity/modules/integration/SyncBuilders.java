package io.github.rudeyeti.necessity.modules.integration;

import github.scarsz.discordsrv.dependencies.jackson.databind.JsonNode;
import github.scarsz.discordsrv.dependencies.jackson.databind.ObjectMapper;
import github.scarsz.discordsrv.dependencies.jackson.databind.node.ArrayNode;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.okhttp3.OkHttpClient;
import github.scarsz.discordsrv.dependencies.okhttp3.Request;
import github.scarsz.discordsrv.dependencies.okhttp3.ResponseBody;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncBuilders {
    protected static int membersSize;

    protected synchronized static ResponseBody getWebsite() {
        try {
            Request request = new Request.Builder()
                    .url("https://buildtheearth.net/api/v1/members")
                    .header("User-Agent", "Necessity")
                    .header("Host", "buildtheearth.net")
                    .header("Authorization", "Bearer " + Config.get.apiKey)
                    .header("Accept", "application/json")
                    .build();
            return new OkHttpClient().newCall(request).execute().body();
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    protected synchronized static ArrayNode getBuilders(ResponseBody responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonObject = mapper.readTree(responseBody.string());
            return (ArrayNode) jsonObject.get("members");
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    protected synchronized static List<Member> getWebsiteMembersList() {
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
