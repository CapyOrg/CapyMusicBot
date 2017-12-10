package org.capy.musicbot.service.entries;

public class Artist {

    public static final long UNKNOWN_ID = -1;

    private String name;
    private String mbid;
    private String url;
    private String image;
    private String shortDescription;
    private String description;
    private long discogsId = UNKNOWN_ID;

    public Artist(String name, String mbid) {
        this.name = name;
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public String getMbid() {
        return mbid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getDiscogsId() {
        return discogsId;
    }

    public void setDiscogsId(long discogsId) {
        this.discogsId = discogsId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", mbid='" + mbid + '\'' +
                ", url='" + url + '\'' +
                ", image='" + image + '\'' +
                ", discogsId=" + discogsId +
                ", shortDescription='" + shortDescription + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
