package org.capy.musicbot.service;

import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Release;

import java.time.Instant;
import java.util.List;

public interface ServiceApi {

    /**
     * Looks for artists by given name.
     * The first one (if exists) will be automatically supplemented by info ({@link #supplementByInfo(Artist)})
     *
     * @param name estimated artist's name
     * @return {@link ServiceResponse} with {@link List} of found artists
     * @throws ServiceException if something went wrong
     */
    ServiceResponse<List<Artist>> findArtist(String name) throws ServiceException;

    /**
     * Supplements the artist by info (biography, etc) from music services
     *
     * @param artist found artist
     * @return {@link ServiceResponse} with supplemented {@link Artist}
     * @throws ServiceException if something went wrong
     */
    ServiceResponse<Artist> supplementByInfo(Artist artist) throws ServiceException;

    /**
     * Connects the given artist with all of existing music services
     *
     * @param artist completed artist
     * @return {@link ServiceResponse} with completed {@link Artist}
     * @throws ServiceException if something went wrong
     */
    ServiceResponse<Artist> checkOutWith(Artist artist) throws ServiceException;

    ServiceResponse<List<Event>> getEvents(Artist artist) throws ServiceException;

    ServiceResponse<List<Release>> getLastReleases(Artist artist, Instant since) throws ServiceException;

    ServiceResponse<List<Release>> getLastReleases(Artist artist, Release since) throws ServiceException;

}
