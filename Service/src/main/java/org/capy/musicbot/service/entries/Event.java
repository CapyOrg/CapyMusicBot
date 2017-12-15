package org.capy.musicbot.service.entries;

import java.time.Instant;
import java.util.Objects;

public class Event {

    public enum Type {
        CONCERT, FESTIVAL
    }

    private Type type;
    private String name;
    private SimpleLocation location;
    private String uri;
    private long id;
    private Venue venue;
    private Instant date;

    public Event(long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleLocation getLocation() {
        return location;
    }

    public void setLocation(SimpleLocation location) {
        this.location = location;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public static class Venue extends Location {

        private String name;

        public Venue(long id) {
            super(id);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class SimpleLocation {
        private String city;
        private double latitude, longitude;

        public SimpleLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", id=" + id +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, location, uri, venue, date);
    }
}
