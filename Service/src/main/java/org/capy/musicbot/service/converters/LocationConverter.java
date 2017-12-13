package org.capy.musicbot.service.converters;

import org.capy.musicbot.service.entries.Location;

public class LocationConverter implements EntryConverter<ru.blizzed.opensongkick.models.Location, Location> {

    @Override
    public Location convert(ru.blizzed.opensongkick.models.Location entry) {
        Location location = new Location(entry.getMetroArea().getId());
        return join(location, entry);
    }

    @Override
    public Location join(Location entry, ru.blizzed.opensongkick.models.Location source) {
        entry.setCity(source.getMetroArea().getDisplayName());
        entry.setCoordinates(source.getMetroArea().getLatitude(), source.getMetroArea().getLongitude());
        entry.setCountry(source.getMetroArea().getCountry().getDisplayName());
        if (source.getMetroArea().getState() != null)
            entry.setState(source.getMetroArea().getState().getDisplayName());
        return entry;
    }
}
