package org.capy.musicbot.service.converters;

import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Release;
import ru.blizzed.discogsdb.model.Image;
import ru.blizzed.discogsdb.model.release.Format;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
        entry.setDate(parseDate(source));

        List<Release.Type> types = new ArrayList<>();
        List<Format> formats = source.getFormats();
        formats.forEach(format -> {
            if (format.getDescriptions() == null) types.add(Release.Type.of(format.getName()));
            else types.addAll(format.getDescriptions()
                    .stream()
                    .map(Release.Type::of)
                    .collect(Collectors.toList())
            );
        });
        entry.setTypes(types);

        if (source.getImages() != null) {
            Image image = getImage(source.getImages(), "primary");
            if (image == null) image = getImage(source.getImages(), "secondary");
            if (image != null) entry.setImage(image.getUri());
        }

        return entry;
    }

    private Image getImage(List<Image> images, String type) {
        return images.stream()
                .filter(i -> i.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    private Instant parseDate(ru.blizzed.discogsdb.model.release.Release source) {
        try {
            return LocalDate.parse(source.getReleased()).atStartOfDay().toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            return ZonedDateTime.parse(source.getDateAdded()).toInstant();
        }
    }
}
