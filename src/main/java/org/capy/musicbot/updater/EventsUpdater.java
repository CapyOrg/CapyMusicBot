package org.capy.musicbot.updater;

import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks artist for events updates rely on subscribers {@link Location}
 *
 * @author BlizzedRu
 */
public class EventsUpdater extends Updater<Event, Artist> {

    public EventsUpdater(Service service, long timeout) {
        super(service, timeout);
    }

    @Override
    protected List<Event> getUpdates(Artist artist) throws ServiceException {
        List<Location> locations = artist.getSubscribers().stream()
                .map(User::getLocation)
                .distinct()
                .collect(Collectors.toList());

        List<Event> updates = new ArrayList<>();
        for (Location location : locations) {
            updates.addAll(service.getEvents(artist.toServiceArtist(), location).getContent());
            takeTimeout();
        }

        return updates;
    }
}
