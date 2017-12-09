package org.capy.musicbot.service.converters;

import org.capy.musicbot.service.entries.Artist;
import ru.blizzed.openlastfm.models.artist.ArtistInfo;

public class ArtistInfoConverter implements EntryConverter<ArtistInfo, Artist> {

    @Override
    public Artist convert(ArtistInfo entry) {
        Artist artist = new Artist(entry.getName(), entry.getMbid());
        return join(artist, entry);
    }

    @Override
    public Artist join(Artist entry, ArtistInfo source) {
        entry.setDescription(source.getBio().getContent());
        entry.setShortDescription(source.getBio().getSummary());
        return entry;
    }
}
