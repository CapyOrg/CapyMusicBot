package org.capy.musicbot.notifier;

import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Release;

import java.util.List;

public interface Notifier {

    void notifyReleases(Artist artist, List<Release> releases);

    void notifyEvents(Artist artist, List<Event> events);

}
