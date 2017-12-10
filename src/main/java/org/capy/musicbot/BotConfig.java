package org.capy.musicbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by enableee on 10.12.17.
 */
public class BotConfig {
    private static final String PROPERTIES_FILE = "src/main/resources/bot.properties";

    private static String botToken;
    private static String botUsername;

    public static void loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(PROPERTIES_FILE)));
        botToken = properties.getProperty("bot.token");
        botUsername = properties.getProperty("bot.username");
    }

    protected static String getBotToken() {
        return botToken;
    }

    protected static String getBotUsername() {
        return botUsername;
    }
}
