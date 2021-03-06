package net.polarizedions.polarizedbot.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {
    private static final Pattern VALID_ARG_NAME = Pattern.compile("[a-zA-Z0-9-]+");
    private static final Pattern MATCH_ARGS = Pattern.compile("(\\{(" + VALID_ARG_NAME.pattern() + ")})");
    private static final Logger logger = LogManager.getLogger("Formatter");

    private Map<String, Supplier<String>> args;

    public Formatter() {
        this.args = new HashMap<>();
    }

    public Formatter addArg(String key, String value) {
        if (! VALID_ARG_NAME.matcher(key).matches()) {
            logger.warn("Non valid argument named '{}'! Ignoring.", key);
            return this;
        }

        this.args.put(key, () -> value);
        return this;
    }

    public Formatter addArg(String key, Supplier<String> value) {
        if (! VALID_ARG_NAME.matcher(key).matches()) {
            logger.warn("Non valid argument named '{}'! Ignoring.", key);
            return this;
        }

        this.args.put(key, value);
        return this;
    }

    public String getArg(String key) {
        Supplier<String> supplier = this.args.get(key);
        return supplier == null ? null : supplier.get();
    }

    public String format(String value) {
        Matcher matcher = MATCH_ARGS.matcher(value);
        while (matcher.find()) {
            String val = this.getArg(matcher.group(2));
            if (val != null) {
                value = value.replaceAll(matcher.group(1).replace("{", "\\{"), val);
            }
        }

        return value;
    }
}
