package org.capy.musicbot.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by enableee on 10.12.17.
 */
public class DBConfig {
    private static final String PROPERTIES_FILE = "src/main/resources/db.properties";

    private static String databaseName;
    private static String host;
    private static int port;

    public static void loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(PROPERTIES_FILE)));
        databaseName = properties.getProperty("db.name");
        host = properties.getProperty("db.host");
        port = Integer.parseInt(properties.getProperty("db.port"));
    }

    protected static String getDatabaseName() {
        return databaseName;
    }

    protected static String getHost() {
        return host;
    }

    protected static int getPort() {
        return port;
    }
}
