package net.polarizedions.polarizedbot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Localizer {
    private static final Logger logger = LogManager.getLogger("Localizer");
    private static final JsonParser parser = new JsonParser();
    public static final String[] AVAILABLE_LANGS = new String[] {
            "en",
    };
    private static Map<String, Map<String, String>> langData;
    private static String currentLang = AVAILABLE_LANGS[0];

    public static void init() {
        logger.info("Loading localization files...");
        langData = new HashMap<>();
        for (String langCode : AVAILABLE_LANGS) {
            loadLangFile(langCode);
        }
    }

    public static boolean supports(String langCode) {
        for (String lc : AVAILABLE_LANGS) {
            if (lc.equalsIgnoreCase(langCode)) {
                return true;
            }
        }
        return false;
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
        catch (JSONException |JsonSyntaxException ex) {
            logger.error("Failed to parse language file for '{}'", ex);
            return;
        }

        logger.debug("Loaded language file for '{}'", langCode);
    }

    private static void parseLangData(String lang, String key, JsonElement object) {
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
//            System.out.println("put '" + key + "' = '" + value + "'");
        }
    }

    public static String localize(String key, Object... values) {
        Map<String, String> langFile = langData.get(currentLang);
        String translated = langFile == null ? null : (langFile.get(key));
        if (translated == null) {
            String context = Arrays.toString(values);
            logger.warn("Unable to translate '{}' for lang {}. Context: {}", key, currentLang, context);
            translated = key + (values.length == 0 ? "" : "#" + context);
        }
        return String.format(translated, values);
    }

    public static void setCurrentLang(String newLang) {
        for (String lang : AVAILABLE_LANGS) {
            if (lang.equalsIgnoreCase(newLang)) {
                currentLang = lang;
                return;
            }
        }
    }
}
