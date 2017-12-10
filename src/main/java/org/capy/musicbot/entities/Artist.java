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

    private String artistName;

    @Reference
    private List<User> subscribers = new ArrayList<>();

    public Artist() {}

    public Artist(String artistName) {
            this.artistName = artistName.toLowerCase();
        }

    public String getArtistName() {
            return artistName;
        }
        @Override
        public String toString() {
            return "Artist{" +
                    "artistName='" + artistName + '\'' +
                    '}';
    }
}
