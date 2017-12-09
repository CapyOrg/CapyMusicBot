package org.capy.musicbot.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enableee on 10.12.17.
 */
public class Artist {
        private String artistName;

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
