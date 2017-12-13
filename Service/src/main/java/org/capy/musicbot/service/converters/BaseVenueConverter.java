package org.capy.musicbot.service.converters;

import org.capy.musicbot.service.entries.Event;
import ru.blizzed.opensongkick.models.BaseVenue;

public class BaseVenueConverter implements EntryConverter<BaseVenue, Event.Venue> {

    @Override
    public Event.Venue convert(BaseVenue entry) {
        return join(new Event.Venue(entry.getId()), entry);
    }

    @Override
    public Event.Venue join(Event.Venue entry, BaseVenue source) {
        entry.setName(source.getDisplayName());
        entry.setCoordinates(entry.getLatitude(), entry.getLongitude());
        entry.setCity(source.getMetroArea().getDisplayName());
        entry.setCountry(source.getMetroArea().getCountry().getDisplayName());
        return entry;
    }
}
