package org.capy.musicbot.updater;

import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Event;

import java.util.List;

public class EventsUpdater extends Updater<Event, Artist> {

    @Override
    protected List<Event> getUpdates(Artist artist) throws ServiceException {
        for (User user : artist.getSubscribers()) {
            
        }
        return null;
    }
}
