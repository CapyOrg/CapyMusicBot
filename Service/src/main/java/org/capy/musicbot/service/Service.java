package org.capy.musicbot.service;

import org.capy.musicbot.service.converters.ArtistInfoConverter;
import org.capy.musicbot.service.converters.EntryConverter;
import org.capy.musicbot.service.converters.FoundArtistConverter;
import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Release;
import ru.blizzed.openlastfm.ApiRequestException;
import ru.blizzed.openlastfm.ApiResponseException;
import ru.blizzed.openlastfm.methods.ApiArtist;
import ru.blizzed.openlastfm.methods.ApiResponse;
import ru.blizzed.openlastfm.models.SearchResult;
import ru.blizzed.openlastfm.models.artist.ArtistInfo;
import ru.blizzed.openlastfm.models.artist.FoundArtist;
import ru.blizzed.openlastfm.params.LastFMParams;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Service implements ServiceApi {

    Service() {
    }

    public ServiceResponse<List<Artist>> findArtist(String name) throws ServiceException {
        try {
            SearchResult<FoundArtist> searchResult = ApiArtist.search().withParams(LastFMParams.ARTIST.of(name)).execute().getContent();
            EntryConverter<FoundArtist, Artist> foundConverter = new FoundArtistConverter();
            List<Artist> artists = searchResult.getItems()
                    .stream()
                    .filter(a -> !a.getMbid().isEmpty())
                    .map(foundConverter::convert)
                    .collect(Collectors.toList());
            return new ServiceResponse<>(artists, true);
        } catch (ApiResponseException | ApiRequestException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ServiceResponse<List<Event>> getEvents(Artist artist) {
        return new ServiceResponse<>(Collections.emptyList(), true);
    }

    @Override
    public ServiceResponse<List<Release>> getLastReleases(Artist artist, Date since) {
        return new ServiceResponse<>(Collections.emptyList(), true);
    }

    @Override
    public ServiceResponse<Artist> getArtistInfo(Artist artist) throws ServiceException {
        try {
            EntryConverter<ArtistInfo, Artist> infoConverter = new ArtistInfoConverter();
            ApiResponse<ArtistInfo> response = ApiArtist.getInfo().withParams(LastFMParams.ARTIST.of(artist.getName())).execute();
            infoConverter.join(artist, response.getContent());
            return new ServiceResponse<>(artist, true);
        } catch (ApiResponseException | ApiRequestException e) {
            throw new ServiceException(e);
        }
    }

}
