package org.capy.musicbot.database;

import com.mongodb.MongoClient;
import org.capy.musicbot.commands.BotCommand;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.entries.Location;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enableee on 10.12.17.
 */
public class MongoManager implements DBManager{
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

    public boolean updateUserState(User user) {
        dropUser(user.getId());
        return addUser(user);
    }

    public UpdateResults addCommandToCommandsList(long id, BotCommand command) {
        Query<User> query = datastore.createQuery(User.class);
        UpdateOperations<User> updateOperations =
                datastore.createUpdateOperations(User.class)
                        .addToSet("commands", command);
        UpdateResults results = datastore.update(query.field("_id").equal(id), updateOperations);
        return results;
    }

    public UpdateResults updateCommandState(long id, BotCommand command) {
        finishLastCommand(id);
        return addCommandToCommandsList(id, command);
    }

    public void finishLastCommand(long id) {
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
                        .set("notificationModeOn", modeOn);
        UpdateResults updateResults = datastore.update(query.field("_id").equal(id), updateOperations);
        return updateResults;
    }

    public UpdateResults setUserLocation(long id, Location location) {
        Query<User> query = datastore.createQuery(User.class);
        UpdateOperations<User> updateOperations =
                datastore.createUpdateOperations(User.class)
                        .set("location", location);
        UpdateResults updateResults = datastore.update(query.field("_id").equal(id), updateOperations);
        return updateResults;
    }

    public boolean addArtist(Artist artist) {
        if (findArtistByMbid(artist.getMbid()) == null) {
            datastore.save(artist);
            return true;
        }
        return false;
    }

    public Artist findArtistByMbid(String mbid) {
        Query<Artist> query = datastore.createQuery(Artist.class);
        Artist artist = query.field("_id").equal(mbid).get();
        return artist;
    }

    @Override
    public Artist findArtistByName(String name) {
        Query<Artist> query = datastore.createQuery(Artist.class);
        Artist artist = query.field("name").equal(name).get();
        return artist;
    }

    public List<Artist> getArtistsList() {
        List<Artist> artists = new ArrayList<>();
        Query<Artist> query = datastore.createQuery(Artist.class);
        artists = query.asList();
        return artists;
    }

    public UpdateResults subscribeUser(long id, String mbid) {
        Artist artist = findArtistByMbid(mbid);
        Query<User> query = datastore.createQuery(User.class);

        UpdateOperations<User> updateOperations =
                datastore.createUpdateOperations(User.class)
                        .addToSet("subscribes", artist);
        UpdateResults results = datastore.update(query.field("_id").equal(id), updateOperations);
        addSubcriberToArtist(id, mbid);
        return results;
    }

    public UpdateResults unsubscribeUser(long id, String mbid) {
        Artist artist = findArtistByMbid(mbid);
        Query<User> query = datastore.createQuery(User.class);

        UpdateOperations<User> updateOperations =
                datastore.createUpdateOperations(User.class)
                        .removeAll("subscribes", artist);
        UpdateResults results = datastore.update(query.field("_id").equal(id), updateOperations);
        removeSubscriberFromArtist(id, mbid);
        return results;
    }

    public List<Artist> getUserSubscribesList(long id) {
        Query<User> query = datastore.createQuery(User.class);
        User user = query.field("_id").equal(id).get();
        return user.getSubscribes();
    }

    public List<User> getArtistSubscribersList(String mbid) {
        Query<Artist> query = datastore.createQuery(Artist.class);
        Artist artist = query.field("_id").equal(mbid).get();
        return artist.getSubscribers();
    }

    public boolean isUserSubscribedOnArtist(long id, String mbid) {
        Artist artist = findArtistByMbid(mbid);
        if (artist == null) return false;

        Query<User> query = datastore
                .createQuery(User.class)
                .field("_id").equal(id)
                .filter("subscribes elem", artist);

        User user = query.get();
        if (user == null) return false;

        return true;
    }

    private UpdateResults addSubcriberToArtist(long id, String mbid) {
        User user = findUser(id);

        Query<Artist> query = datastore.createQuery(Artist.class);

        UpdateOperations<Artist> updateOperations =
                datastore.createUpdateOperations(Artist.class)
                        .addToSet("subscribers", user);
        UpdateResults results = datastore.update(query.field("_id").equal(mbid), updateOperations);
        return results;
    }

    public UpdateResults removeSubscriberFromArtist(long id, String mbid) {
        User user = findUser(id);

        Query<Artist> query = datastore.createQuery(Artist.class);

        UpdateOperations<Artist> updateOperations =
                datastore.createUpdateOperations(Artist.class)
                        .removeAll("subscribers", user);
        UpdateResults results = datastore.update(query.field("_id").equal(mbid), updateOperations);
        return results;
    }

    public boolean dropArtist(String mbid) {
        Query<Artist> query = datastore.createQuery(Artist.class);
        datastore.delete(query.field("_id").equal(mbid));

        if (findArtistByMbid(mbid) == null)
            return true;
        else
            return false;
    }
}
