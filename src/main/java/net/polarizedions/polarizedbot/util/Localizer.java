package net.polarizedions.polarizedbot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Localizer {
    public static final String[] AVAILABLE_LANGUAGES = new String[] {
            "en",
    };
    private static final Logger logger = LogManager.getLogger("Localizer");
    private static final JsonParser parser = new JsonParser();
    private static Map<String, Map<String, String>> langData;


    private String currentLang;

    public Localizer(@NotNull IMessage message) {
        this(message.getGuild());
    }

    public Localizer(IGuild guild) {
        this(guild == null ? AVAILABLE_LANGUAGES[0] : GuildManager.getConfig(guild).lang);
    }

    public Localizer(String lang) {
        for (String l : AVAILABLE_LANGUAGES) {
            if (l.equalsIgnoreCase(lang)) {
                this.currentLang = l;
                break;
            }
        }

        if (this.currentLang == null) {
            logger.debug("Failed to create localizer for language {}, defauling to {}", lang, AVAILABLE_LANGUAGES[0]);
            this.currentLang = AVAILABLE_LANGUAGES[0];
        }
    }

    public static void init() {
        logger.info("Loading localization files...");
        langData = new HashMap<>();
        for (String langCode : AVAILABLE_LANGUAGES) {
            loadLangFile(langCode);
        }
    }

    private static void loadLangFile(String langCode) {
        String path = "/lang/" + langCode + ".json";
        InputStream is = Localizer.class.getResourceAsStream(path);
        if (is == null) {
            logger.error("Failed to loadGlobalConfig language file {}", path);
            return;
        }

        try {
            JsonElement object = parser.parse(new InputStreamReader(is));
            langData.put(langCode, new HashMap<>());
            parseLangData(langCode, "", object);
        }
        catch (JSONException | JsonSyntaxException ex) {
            logger.error("Failed to parse language file for '{}'", ex);
            return;
        }

        logger.debug("Loaded language file for '{}'", langCode);
    }

    private static void parseLangData(String lang, String key, @NotNull JsonElement object) {
        if (object.isJsonObject()) {
            String keyPrefix = key.length() == 0 ? "" : key + ".";
            for (Map.Entry<String, JsonElement> child : object.getAsJsonObject().entrySet()) {
                parseLangData(lang, keyPrefix + child.getKey(), child.getValue());
            }
        }
        else {
            String value = object.getAsString();
            if (key.endsWith("*")) {
                key = key.substring(0, key.length() - 2);
            }
            langData.get(lang).put(key, value);
        }
    }

    @Contract(pure = true)
    public static boolean supports(String langCode) {
        for (String lc : AVAILABLE_LANGUAGES) {
            if (lc.equalsIgnoreCase(langCode)) {
                return true;
            }
        }
        return false;
    }

    public String getCurrentLang() {
        return this.currentLang;
    }

    public void setCurrentLang(String newLang) {
        for (String lang : AVAILABLE_LANGUAGES) {
            if (lang.equalsIgnoreCase(newLang)) {
                this.currentLang = lang;
                return;
            }
        }
    }

    public String localize(String key, Object... values) {
        Map<String, String> langFile = langData.get(this.currentLang);
        String translated = langFile == null ? null : ( langFile.get(key) );
        if (translated == null) {
            String context = Arrays.toString(values);
            logger.warn("Unable to translate '{}' for lang {}. Context: {}", key, this.currentLang, context);
            translated = key + ( values.length == 0 ? "" : "#" + context );
        }
        return String.format(translated, values);
    }

    public boolean doesKeyExist(String key) {
        Map<String, String> langFile = langData.get(this.currentLang);
        return langFile != null && ( langFile.containsKey(key) );
    }
}
