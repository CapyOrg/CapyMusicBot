package org.capy.musicbot.entities;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enableee on 10.12.17.
 */

@Entity(value = "artists")
public class Artist {
    @Id
    private String mbid;

    private String name;

    @Reference
    private List<User> subscribers = new ArrayList<>();

    public Artist() {}

    public Artist(String mbid, String artistName) {
        this.mbid = mbid;
        this.name = artistName;
    }

    public String getMbid() {
        return mbid;
    }

    public String getName() {
            return name;
        }

    public List<User> getSubscribers() {
        return subscribers;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                '}';
    }
}
