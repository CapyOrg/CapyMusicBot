package org.capy.musicbot.service.converters;

import org.capy.musicbot.service.entries.Event;
import ru.blizzed.opensongkick.models.Event.SimpleLocation;

public class SimpleLocationConverter implements EntryConverter<ru.blizzed.opensongkick.models.Event.SimpleLocation, Event.SimpleLocation> {

    @Override
    public Event.SimpleLocation convert(SimpleLocation entry) {
        return join(new Event.SimpleLocation(entry.getLatitude(), entry.getLongitude()), entry);
    }

    @Override
    public Event.SimpleLocation join(Event.SimpleLocation entry, SimpleLocation source) {
        entry.setCity(source.getCity());
        return entry;
    }
}
