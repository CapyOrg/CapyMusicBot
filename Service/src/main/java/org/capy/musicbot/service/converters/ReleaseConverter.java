package org.capy.musicbot.service.converters;

import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Release;
import ru.blizzed.discogsdb.model.Image;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class ReleaseConverter implements EntryConverter<ru.blizzed.discogsdb.model.release.Release, Release> {

    private Artist artist;

    public ReleaseConverter(Artist artist) {
        this.artist = artist;
    }

    @Override
    public Release convert(ru.blizzed.discogsdb.model.release.Release entry) {
        Release release = new Release(artist, entry.getId());
        return join(release, entry);
    }

    @Override
    public Release join(Release entry, ru.blizzed.discogsdb.model.release.Release source) {
        entry.setTitle(source.getTitle());
        entry.setDate(Instant.parse(source.getDateAdded()));
        List<Release.Type> types = source.getFormats().get(0).getDescriptions()
                .stream()
                .map(String::toUpperCase)
                .map(Release.Type::valueOf)
                .collect(Collectors.toList());
        entry.setTypes(types);

        Image image = source.getImages()
                .stream()
                .filter(i -> i.getType().equals("primary"))
                .findFirst()
                .orElse(null);

        if (image != null) entry.setImage(image.getUri());
        return entry;
    }
}
