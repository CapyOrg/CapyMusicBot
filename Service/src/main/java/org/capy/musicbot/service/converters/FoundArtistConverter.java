package org.capy.musicbot.service.converters;

import org.capy.musicbot.service.entries.Artist;
import ru.blizzed.openlastfm.models.artist.FoundArtist;
import ru.blizzed.openlastfm.models.commons.Image;

public class FoundArtistConverter implements EntryConverter<FoundArtist, Artist> {

    @Override
    public Artist convert(FoundArtist foundArtist) {
        Artist artist = new Artist(foundArtist.getName(), foundArtist.getMbid());
        return join(artist, foundArtist);
    }

    @Override
    public Artist join(Artist artist, FoundArtist source) {
        artist.setImage(source.getImage().getUrl(Image.Size.MEGA));
        artist.setUrl(source.getUrl());
        return artist;
    }

}
