package org.capy.musicbot.service.converters;


import org.capy.musicbot.service.entries.Event;

import java.time.*;
import java.time.format.DateTimeParseException;

public class EventConverter implements EntryConverter<ru.blizzed.opensongkick.models.Event, Event> {

    @Override
    public Event convert(ru.blizzed.opensongkick.models.Event entry) {
        return join(new Event(entry.getId()), entry);
    }

    @Override
    public Event join(Event entry, ru.blizzed.opensongkick.models.Event source) {
        entry.setType(Event.Type.valueOf(source.getType().name().toUpperCase()));
        entry.setUri(source.getUri());
        entry.setName(source.getDisplayName());
        entry.setVenue(new BaseVenueConverter().convert(source.getVenue()));
        entry.setLocation(new SimpleLocationConverter().convert(source.getLocation()));
        try {
            String date = source.getStart().getDateTime();
            date = date == null ? source.getStart().getDate() : date;
            entry.setDate(ZonedDateTime.parse(date).toInstant());
        } catch (DateTimeParseException e) {
            source.getStart();
            LocalDate date = LocalDate.parse(source.getStart().getDate());
            LocalTime time = source.getStart().getTime() == null ? LocalTime.MIDNIGHT : LocalTime.parse(source.getStart().getTime());
            entry.setDate(LocalDateTime.of(date, time).toInstant(ZoneOffset.UTC));
        }
        return entry;
    }

}
