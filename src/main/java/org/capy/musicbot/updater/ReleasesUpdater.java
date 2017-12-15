package org.capy.musicbot.updater;

import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Release;

import java.util.Collections;
import java.util.List;

public class ReleasesUpdater extends Updater<Release, Artist> {

    public ReleasesUpdater(Service service, long timeout) {
        super(service, timeout);
    }

    @Override
    protected List<Release> getUpdates(Artist artist) throws ServiceException {
        if (artist.getDiscogsId() == -1)
            return Collections.emptyList();
        return service.getLastReleases(artist.toServiceArtist(), artist.getLastRelease()).getContent();
    }
}
