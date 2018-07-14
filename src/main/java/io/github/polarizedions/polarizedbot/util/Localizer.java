package io.github.polarizedions.polarizedbot.util;

import io.github.polarizedions.polarizedbot.wrappers.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Localizer {
    private static final Logger logger = LogManager.getLogger("Localizer");
    private static final String[] AVAILABLE_LANGS = new String[] {
            "en",
            "nl"
    };
    private Map<String, Properties> langData;

    public Localizer() {
        langData = new HashMap<>();

        for (String langCode : AVAILABLE_LANGS) {
            loadLangFile(langCode);
        }
    }

    public boolean supports(String langCode) {
        langCode = langCode.toLowerCase().trim();
        for (String lc : AVAILABLE_LANGS) {
            if (lc.equals(langCode)) {
                return true;
            }
        }
        return false;
    }

    private void loadLangFile(String langCode) {
        String path = "/lang/" + langCode + ".properties";
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            logger.error("Failed to load language file {}", path);
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

    public String localize(String lang, String key, String... values) {
        Properties langFile = langData.get(lang);
        String translated = langFile == null ? null : (langFile.getProperty(key));
        if (translated == null) {
            logger.debug("Unable to translate '{}' for lang {}", key, lang);
            translated = key;
        }
        return String.format(translated, (Object[]) values);
    }

    public String localize(Guild guild, String key, String... values) {
        return localize(guild.getConfig().lang, key, values);
    }
}
