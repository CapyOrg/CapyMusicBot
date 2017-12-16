package org.capy.musicbot.service.converters;

import org.capy.musicbot.service.entries.Artist;
import ru.blizzed.openlastfm.models.commons.Image;

public class ArtistConverter implements EntryConverter<ru.blizzed.openlastfm.models.artist.Artist, Artist> {

    @Override
    public Artist convert(ru.blizzed.openlastfm.models.artist.Artist entry) {
        Artist artist = new Artist(entry.getName(), entry.getMbid());
        return join(artist, entry);
    }

    @Override
    public Artist join(Artist entry, ru.blizzed.openlastfm.models.artist.Artist source) {
        entry.setImage(source.getImage().getUrl(Image.Size.MEGA));
        return entry;
    }

}
