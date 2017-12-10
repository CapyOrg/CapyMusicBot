package org.capy.musicbot.service;

import ru.blizzed.discogsdb.DiscogsAuthData;
import ru.blizzed.discogsdb.DiscogsDBApi;
import ru.blizzed.openlastfm.OpenLastFMContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ServiceContext {

    private static final String PROPERTIES_FILE = "Service/src/main/resources/service.properties";
    private static Service service;

    private ServiceContext() {
    }

    public static Service getService() {
        if (service == null) {
            init();
            service = new Service();
        }
        return service;
    }

    private static void init() {
        try {
            ServiceProperties properties = new ServiceProperties();
            OpenLastFMContext.initialize(properties.lastFMApiKey);
            DiscogsDBApi.initialize(new DiscogsAuthData(properties.discogsApiKey, properties.discogsApiSecret));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ServiceProperties {
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


