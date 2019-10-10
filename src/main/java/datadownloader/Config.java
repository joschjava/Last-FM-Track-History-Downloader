package datadownloader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public class Config {
    public static String API_KEY;
    public static String USERNAME;
    public static String SAVE_FILE;
    public static String RESPONSE_LOG_FOLDER;
    public static String CSV_DELIMITER = ";";

    /**
     * Reads configuration from settings.properties, see README.md for info
     */
    public static void loadConfig() {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName("settings.properties"));
        try {
            Configuration config = builder.getConfiguration();
            API_KEY = config.getString("apiKey");
            USERNAME = config.getString("username");
            SAVE_FILE = config.getString("saveFile");
            RESPONSE_LOG_FOLDER = config.getString("responseLogFolder");
        } catch (ConfigurationException e) {
            log.error("loading of the configuration file failed:");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
