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

    private long discogsId;

    @Reference
    private List<User> subscribers = new ArrayList<>();

    public Artist() {}

    public Artist(String mbid, String artistName) {
        this.mbid = mbid;
        this.name = artistName;
    }

    public Artist(org.capy.musicbot.service.entries.Artist artist) {
        this.mbid = artist.getMbid();
        this.name = artist.getName();
        this.discogsId = artist.getDiscogsId();
    }

    public String getMbid() {
        return mbid;
    }

    public String getName() {
            return name;
        }

    public long getDiscogsId() {
        return discogsId;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "mbid='" + mbid + '\'' +
                ", name='" + name + '\'' +
                ", discogsId=" + discogsId +
                ", subscribers=" + subscribers +
                '}';
    }
}
