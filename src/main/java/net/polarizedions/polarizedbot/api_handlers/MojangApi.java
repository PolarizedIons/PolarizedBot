package net.polarizedions.polarizedbot.api_handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.polarizedions.polarizedbot.util.WebHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MojangApi {
    private static final String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private static final Logger logger = LogManager.getLogger("MinecraftApi");

    public static MinecraftVersions fetchLatestVersions() {
        logger.debug("Fetching latest minecraft versions");

        JsonElement versionManifest = WebHelper.fetchJson(VERSION_MANIFEST_URL);
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
