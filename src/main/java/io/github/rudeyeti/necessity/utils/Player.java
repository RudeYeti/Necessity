package io.github.rudeyeti.necessity.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class Player {
    public static boolean exists(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            URLConnection connection = url.openConnection();
            connection.getContent().toString().isEmpty();
        } catch (IOException | NullPointerException error) {
            return false;
        }
        return true;
    }

    public static boolean isUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException error) {
            return false;
        }
    }
}
