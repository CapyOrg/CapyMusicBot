package org.capy.musicbot.service.entries;

import java.time.Instant;
import java.util.List;

public class Release {

    public enum Type {
        ALBUM, SINGLE, EP, LP
    }

    public enum DCType {
        RELEASE, MASTER;
    }

    private List<Type> types;
    private DCType dcType;

    private String title;
    private Artist artist;
    private long id;
    private Instant date;
    private int year;

    public List<Type> getTypes() {
        return types;
    }
    private String image;

    public void setTypes(List<Type> types) {
        this.types = types;
    }

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

    public DCType getDcType() {
        return dcType;
    }

    public void setDCType(DCType dcType) {
        this.dcType = dcType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Release{" +
                "dcType=" + dcType +
                ", title='" + title + '\'' +
                ", artist=" + artist.getName() +
                ", id=" + id +
                ", date=" + date +
                ", year=" + year +
                ", image='" + image + '\'' +
                '}';
    }
}
