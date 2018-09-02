package net.polarizedions.polarizedbot.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;

public class Args {
    private static final Logger logger = LogManager.getLogger("ArgParser");
    public static Args instance;

    private Args() {
        instance = this;
    }

    @Parameter(names = {"--log"}, description = "Set the logging level")
    public String logLevel = Level.DEBUG.name();

    @Parameter(names = {"--update"}, hidden = true)
    public File updateFile;

    @Parameter(names = "--config", description = "Set the config directory")
    public File configDir;



    public static void handle(String[] argv) {
        try {
            JCommander.newBuilder()
                    .addObject(new Args())
                    .build()
                    .parse(argv);
        }
        catch (ParameterException ex) {
            ex.usage();
            System.exit(1);
        }

        Args args = Args.instance;
        if (!args.logLevel.equals(Level.DEBUG.name())) {
            try {
                Configurator.setRootLevel(Level.getLevel(args.logLevel.toUpperCase()));
                logger.info("Set log level to '{}'", args.logLevel);
            }
            catch (NullPointerException ex) {
                logger.warn("Log level '{}' not found. Using default");
            }
        }

        if (args.updateFile != null) {
                if (!args.updateFile.delete()) {
                    logger.info("Error deleting old jar {} after update!", args.updateFile);
                }
                else {
                    logger.info("Deleted old jar {} because of update", args.updateFile);
                }
        }

        if (args.configDir != null) {
            logger.info("Using {} as config directory", args.configDir.getAbsolutePath());
            ConfigManager.configDir = args.configDir;
        }
    }
}
