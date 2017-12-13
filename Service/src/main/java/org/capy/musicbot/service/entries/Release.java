package org.capy.musicbot.service.entries;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Release {

    public static final long UNKNOWN_ID = -1;

    public enum Type {
        ALBUM, SINGLE, OTHER;

        public static Type of(String str) {
            if (str.toUpperCase().equals(ALBUM.name()))
                return ALBUM;
            else if (str.toUpperCase().equals(SINGLE.name()))
                return SINGLE;
            else return OTHER;
        }

    }

    private List<Type> types;

    private String title;
    private Artist artist;
    private long id = UNKNOWN_ID;
    private long mainId = UNKNOWN_ID;
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

    public long getMainId() {
        return mainId;
    }

    public void setMainId(long mainId) {
        this.mainId = mainId;
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
                "title='" + title + '\'' +
                ", artist=" + artist.getName() +
                ", id=" + id +
                ", date=" + date +
                ", year=" + year +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Release release = (Release) o;
        return id == release.id | (mainId != UNKNOWN_ID & mainId == release.mainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, id, mainId, date, year);
    }
}
