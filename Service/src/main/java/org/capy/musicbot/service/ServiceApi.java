package org.capy.musicbot.service;

import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Release;

import java.util.Date;
import java.util.List;

public interface ServiceApi {

    ServiceResponse<List<Artist>> findArtist(String name) throws ServiceException;

    ServiceResponse<List<Event>> getEvents(Artist artist) throws ServiceException;

    ServiceResponse<List<Release>> getLastReleases(Artist artist, Date since) throws ServiceException;

    ServiceResponse<Artist> getArtistInfo(Artist artist) throws ServiceException;

}
