package io.github.polarizedions.polarizedbot.api_handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MojangApi {
    private static final String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private static final Logger logger = LogManager.getLogger("MinecraftApi");

    public static MinecraftVersions fetchLatestVersions() {
        logger.debug("Fetching latest minecraft versions");
        URL url;
        try {
            url = new URL(VERSION_MANIFEST_URL);
        } catch (MalformedURLException e) {
            logger.error("Error fetching latest versions: Malformed url!", e);
            return null;
        }
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            logger.error("Error fetching latest versions: Can't open stream!", e);
            return null;
        }

        JsonElement versionManifest = new JsonParser().parse(reader);
        if (versionManifest == null) {
            return null;
        }

        JsonObject latest = versionManifest.getAsJsonObject().getAsJsonObject("latest");
        return new MinecraftVersions(latest.get("release").getAsString(), latest.get("snapshot").getAsString());
    }

    public static class MinecraftVersions {
        public String release;
        public String snapshot;

        public MinecraftVersions(String release, String snapshot) {
            this.release = release;
            this.snapshot = snapshot;
        }
    }
}
