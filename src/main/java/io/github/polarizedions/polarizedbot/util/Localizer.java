package io.github.polarizedions.polarizedbot.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Localizer {
    private static final Logger logger = LogManager.getLogger("Localizer");
    public static final String[] AVAILABLE_LANGS = new String[] {
            "en",
    };
    private static Map<String, Properties> langData;
    private static String currentLang = AVAILABLE_LANGS[0];

    public static void init() {
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
        String path = "/lang/" + langCode + ".properties";
        InputStream is = Localizer.class.getResourceAsStream(path);
        if (is == null) {
            logger.error("Failed to loadGlobalConfig language file {}", path);
            return;
        }

        logger.debug("Loading language file for '{}'", langCode);
        Properties lang = new Properties();
        try {
            lang.load(is);
        } catch (IOException e) {
            logger.error("Error loading/parsing language file " + path, e);
            return;
        }
        langData.put(langCode, lang);
    }

    public static String localize(String key, Object... values) {
        Properties langFile = langData.get(currentLang);
        String translated = langFile == null ? null : (langFile.getProperty(key));
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
