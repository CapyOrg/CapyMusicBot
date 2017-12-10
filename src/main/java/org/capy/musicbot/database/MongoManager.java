package org.capy.musicbot.database;

import com.mongodb.MongoClient;
import org.capy.musicbot.commands.BotCommand;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

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

    public boolean addUser(User user) {
        if (findUser(user.getId()) == null) {
            datastore.save(user);
            return true;
        }
        return false;
    }

    public User findUser(long id) {
        Query<User> query = datastore.createQuery(User.class);
        User user = query.field("_id").equal(id).get();
        return user;
    }

    public boolean dropUser(long id) {
        Query<User> query = datastore.createQuery(User.class);
        datastore.delete(query.field("_id").equal(id));
        if (findUser(id) == null)
            return true;
        else
            return false;
    }

    public UpdateResults addCommandToCommandsList(long id, BotCommand command) {
        Query<User> query = datastore.createQuery(User.class);
        UpdateOperations<User> updateOperations =
                datastore.createUpdateOperations(User.class)
                        .addToSet("commands", command);
        UpdateResults results = datastore.update(query.field("_id").equal(id), updateOperations);
        return results;
    }

    public void finishCommand(long id, BotCommand command) {
        if (findUser(id) != null) {
            Query<User> query = datastore.createQuery(User.class);
            UpdateOperations<User> updateOperations =
                    datastore.createUpdateOperations(User.class)
                            .removeLast("commands");
            UpdateResults results = datastore.update(query.field("_id").equal(id), updateOperations);
        }
    }

    public void finishAllCommands(long id) {
        if (findUser(id) != null) {
            while (findUser(id).getCurrentCommand() != null) {
                Query<User> query = datastore.createQuery(User.class);
                UpdateOperations<User> updateOperations =
                        datastore.createUpdateOperations(User.class)
                                .removeLast("commands");
                datastore.update(query.field("_id").equal(id), updateOperations);
            }
        }
    }

    public UpdateResults setUserNotificationsMode(long id, boolean modeOn) {
        Query<User> query = datastore.createQuery(User.class);
        UpdateOperations<User> updateOperations =
                datastore.createUpdateOperations(User.class)
                        .set("notifications_on", modeOn);
        UpdateResults updateResults = datastore.update(query.field("_id").equal(id), updateOperations);
        return updateResults;
    }

    public boolean addArtist(Artist artist) {
        if (findArtist(artist.getName()) == null) {
            datastore.save(artist);
            return true;
        }
        return false;
    }

    public Artist findArtist(String artistName) {
        Query<Artist> query = datastore.createQuery(Artist.class);
        Artist artist = query.field("_id").equal(artistName).get();
        return artist;
    }

    public UpdateResults subscribeUser(long id, String artistName) {
        Artist artist = findArtist(artistName);
        Query<User> query = datastore.createQuery(User.class);

        UpdateOperations<User> updateOperations =
                datastore.createUpdateOperations(User.class)
                        .addToSet("subscribes", artist);
        UpdateResults results = datastore.update(query.field("_id").equal(id), updateOperations);
        addSubcriberToArtist(id, artistName);
        return results;
    }

    public UpdateResults unsubscribeUser(long id, String artistName) {
        Artist artist = findArtist(artistName);
        Query<User> query = datastore.createQuery(User.class);

        UpdateOperations<User> updateOperations =
                datastore.createUpdateOperations(User.class)
                        .removeAll("subscribes", artist);
        UpdateResults results = datastore.update(query.field("_id").equal(id), updateOperations);
        removeSubscriberFromArtist(id, artistName);
        return results;
    }

    public boolean isUserSubscribedOnArtist(long id, String artistName) {
        Artist artist = findArtist(artistName);
        if (artist == null) return false;

        Query<User> query = datastore
                .createQuery(User.class)
                .field("_id").equal(id)
                .filter("subscribes elem", artist);

        User user = query.get();
        if (user == null) return false;

        return true;
    }

    public UpdateResults addSubcriberToArtist(long id, String artistName) {
        User user = findUser(id);

        Query<Artist> query = datastore.createQuery(Artist.class);

        UpdateOperations<Artist> updateOperations =
                datastore.createUpdateOperations(Artist.class)
                        .addToSet("subscribers", user);
        UpdateResults results = datastore.update(query.field("_id").equal(artistName), updateOperations);
        return results;
    }

    public UpdateResults removeSubscriberFromArtist(long id, String artistName) {
        User user = findUser(id);

        Query<Artist> query = datastore.createQuery(Artist.class);

        UpdateOperations<Artist> updateOperations =
                datastore.createUpdateOperations(Artist.class)
                        .removeAll("subscribers", user);
        UpdateResults results = datastore.update(query.field("_id").equal(artistName), updateOperations);
        return results;
    }

    public boolean dropArtist(String artistName) {
        Query<Artist> query = datastore.createQuery(Artist.class);
        datastore.delete(query.field("_id").equal(artistName));

        if (findArtist(artistName) == null)
            return true;
        else
            return false;

    }
}
