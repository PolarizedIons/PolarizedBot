package net.polarizedions.polarizedbot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Localizer {
    public static final ArrayList<String> AVAILABLE_LANGUAGES = new ArrayList<>();
    public static final String DEFAULT_LANGUAGE;

    static {
        DEFAULT_LANGUAGE = "en";
        AVAILABLE_LANGUAGES.add("en");
    }

    private static final Logger logger = LogManager.getLogger("Localizer");
    private static final JsonParser parser = new JsonParser();
    private static Map<String, Map<String, String>> langData;


    private String currentLang;

    public Localizer() {
        this(DEFAULT_LANGUAGE);
    }

    public Localizer(String lang) {
        lang = lang.toLowerCase();

        if (AVAILABLE_LANGUAGES.contains(lang)) {
            this.currentLang = lang;
        }
        else {
            logger.debug("Failed to create localizer for language {}, defaulting to {}", lang, DEFAULT_LANGUAGE);
            this.currentLang = DEFAULT_LANGUAGE;
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
        catch (JsonSyntaxException ex) {
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
    public static boolean supports(@NotNull String langCode) {
        return AVAILABLE_LANGUAGES.contains(langCode.toLowerCase());
    }

    public String getCurrentLang() {
        return this.currentLang;
    }

    public void setCurrentLang(String newLang) {
        newLang = newLang.toLowerCase();

        if (AVAILABLE_LANGUAGES.contains(newLang)) {
            this.currentLang = newLang;
        }
        else {
            logger.debug("Failed to set localizer to {}, defaulting to {}", newLang, DEFAULT_LANGUAGE);
            this.currentLang = DEFAULT_LANGUAGE;
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

    public String localizeNumber(String key, int number, Object... values) {
        return this.localize(this.doesKeyExist(key + "." + number) ? key + "." + number : key, values.length == 0 ? new Object[] {number} : values);
    }

    public String localizeNumber(String key, double number, Object... values) {
        return this.localizeNumber(key, (int) Math.ceil(number), values);
    }

    public boolean doesKeyExist(String key) {
        Map<String, String> langFile = langData.get(this.currentLang);
        return langFile != null && ( langFile.containsKey(key) );
    }
}
