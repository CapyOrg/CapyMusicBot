package org.capy.musicbot.service.entries;

import java.time.Instant;

public class Release {

    private String title;

    private Artist artist;
    private long id;
    private Instant date;
    private Type type;
    private String image;

    public Release(Artist artist, long id) {
        this.artist = artist;
        this.id = id;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public enum Type {
        ALBUM, SINGLE, EP
    }
}
