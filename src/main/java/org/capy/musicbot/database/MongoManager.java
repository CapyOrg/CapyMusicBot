package org.capy.musicbot.database;

import com.mongodb.MongoClient;
import org.capy.musicbot.commands.BotCommand;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by enableee on 10.12.17.
 */
public class MongoManager {
    private static volatile MongoClient mongoClient;
    private static Morphia morphia;
    private static Datastore datastore;
    private static volatile MongoManager instance;

    public MongoManager() {
        if (mongoClient == null)
            mongoClient = new MongoClient(DBConfig.getHost(), DBConfig.getPort());
        morphia = new Morphia();
        this.datastore = morphia.createDatastore(mongoClient, DBConfig.getDatabaseName());
        morphia.map(User.class, Artist.class, BotCommand.class);
    }

    public static MongoManager getInstance() {
        final MongoManager currentInstance;
        if (instance == null) {
            synchronized (MongoManager.class) {
                if (instance == null) {
                    instance = new MongoManager();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }
        return currentInstance;
    }


}
