package org.capy.musicbot.service;

import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Location;
import org.capy.musicbot.service.entries.Release;

import java.time.Instant;
import java.util.List;

public interface ServiceApi {

    /**
     * Looks for artists by given name
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

    /**
     * Gives last releases of given artist since given time
     *
     * @param artist artist whose releases needed
     * @param since  time from which releases will be included
     * @return {@link ServiceResponse} with {@link List} of last artist's releases
     * @throws ServiceException if something went wrong
     */
    ServiceResponse<List<Release>> getLastReleases(Artist artist, Instant since) throws ServiceException;

    /**
     * Gives last releases of given artist since given release
     *
     * @param artist artist whose releases needed
     * @param since  release from which releases will be included
     * @return {@link ServiceResponse} with {@link List} of last artist's releases
     * @throws ServiceException if something went wrong
     */
    ServiceResponse<List<Release>> getLastReleases(Artist artist, Release since) throws ServiceException;

    /**
     * Gives given artist's events around the given location
     *
     * @param artist artist whose events needed
     * @return {@link ServiceResponse} with {@link List} of last artist's events
     * @throws ServiceException if something went wrong
     */
    ServiceResponse<List<Event>> getEvents(Artist artist, Location location) throws ServiceException;

    /**
     * Looks for locations by given query
     *
     * @param query search query
     * @return {@link ServiceResponse} with {@link List} of the nearest locations
     * @throws ServiceException if something went wrong
     */
    ServiceResponse<List<Location>> findLocation(String query) throws ServiceException;

    /**
     * Gives user's artists by his LastFM username
     *
     * @param lastFmUsername user's login in LastFM service
     * @param page the page number to fetch
     * @param perPage the number of results to fetch per page
     * @return {@link ServiceResponse} with {@link List} of user's artists
     * @throws ServiceException if username is incorrect or something else went wrong
     * @throws InvalidUsernameException if username is not found on LastFM service
     */
    ServiceResponse<List<Artist>> getLastFmUserArtists(String lastFmUsername, int page, int perPage) throws ServiceException, InvalidUsernameException;

}
