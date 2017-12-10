package org.capy.musicbot.service.converters;

import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Release;
import ru.blizzed.discogsdb.model.artist.ArtistRelease;

public class ArtistReleaseConverter implements EntryConverter<ArtistRelease, Release> {

    private Artist artist;

    public ArtistReleaseConverter(Artist artist) {
        this.artist = artist;
    }

    @Override
    public Release convert(ArtistRelease entry) {
        Release release = new Release(artist, entry.getId());
        return join(release, entry);
    }

    @Override
    public Release join(Release entry, ArtistRelease source) {
        entry.setTitle(source.getTitle());
        entry.setDCType(Release.DCType.valueOf(source.getType()));
        entry.setYear(source.getYear());
        return entry;
    }
}
