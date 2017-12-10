package org.capy.musicbot.service;

import ru.blizzed.discogsdb.DiscogsAuthData;
import ru.blizzed.discogsdb.DiscogsDBApi;
import ru.blizzed.openlastfm.OpenLastFMContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServiceContext {

    private static final String PROPERTIES_FILE = "Service/src/main/resources/service.properties";
    private static ServiceContext sInstance = new ServiceContext();

    private ServiceProperties properties;

    private ServiceContext() {
        init();
    }

    public static ServiceContext getInstance() {
        return sInstance;
    }

    public static Service getService() {
        return new Service();
    }

    private void init() {
        try {
            properties = new ServiceProperties();
            OpenLastFMContext.initialize(properties.lastFMApiKey);
            DiscogsDBApi.initialize(new DiscogsAuthData(properties.discogsApiKey, properties.discogsApiSecret));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 03.12.2017
        }
    }

    private class ServiceProperties {
        String lastFMApiKey;
        String discogsApiKey;
        String discogsApiSecret;

        ServiceProperties() throws IOException {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(PROPERTIES_FILE)));
            this.lastFMApiKey = properties.getProperty("lastfm.api_key");
            this.discogsApiKey = properties.getProperty("discogs.api_key");
            this.discogsApiSecret = properties.getProperty("discogs.api_secret");
        }
    }

}


