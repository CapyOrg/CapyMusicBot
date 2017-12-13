package org.capy.musicbot.database;

import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.ServiceResponse;
import org.mongodb.morphia.query.Query;

/**
 * Created by enableee on 11.12.17.
 */
public interface DBManager {
    boolean addUser(User user);
    User findUser(long id);
    boolean dropUser(long id);

    boolean addArtist(Artist artist);
    Artist findArtistByMbid(String mbid);
    Artist findArtistByName(String name);
    boolean dropArtist(String mbid);
}
