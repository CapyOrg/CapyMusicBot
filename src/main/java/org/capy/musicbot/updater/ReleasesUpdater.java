package org.capy.musicbot.updater;

import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.service.ServiceException;
import ru.blizzed.discogsdb.model.release.Release;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReleasesUpdater extends Updater<Release, Artist> {

    @Override
    protected List<Release> getUpdates(Artist artist) throws ServiceException {
        if (artist.getDiscogsId() == -1)
            return Collections.emptyList();



        return null;
    }
}
