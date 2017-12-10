package org.capy.musicbot.service;

import org.capy.musicbot.service.converters.ArtistInfoConverter;
import org.capy.musicbot.service.converters.EntryConverter;
import org.capy.musicbot.service.converters.FoundArtistConverter;
import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Release;
import ru.blizzed.discogsdb.DiscogsDBApi;
import ru.blizzed.discogsdb.DiscogsDBCallException;
import ru.blizzed.discogsdb.DiscogsDBErrorException;
import ru.blizzed.discogsdb.model.Page;
import ru.blizzed.discogsdb.model.search.BaseSearchResult;
import ru.blizzed.discogsdb.params.DiscogsDBParams;
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
            SearchResult<FoundArtist> searchResult = ApiArtist.search().withParams(
                    LastFMParams.ARTIST.of(name), LastFMParams.LIMIT.of(15)
            ).execute().getContent();

            EntryConverter<FoundArtist, Artist> foundConverter = new FoundArtistConverter();
            List<Artist> artists = searchResult.getItems()
                    .stream()
                    .filter(a -> !a.getMbid().isEmpty())
                    .map(foundConverter::convert)
                    .collect(Collectors.toList());

            if (!artists.isEmpty()) artists.set(0, supplementByInfo(artists.get(0)).getContent());
            return new ServiceResponse<>(artists, true);
        } catch (ApiResponseException | ApiRequestException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ServiceResponse<Artist> supplementByInfo(Artist artist) throws ServiceException {
        try {
            EntryConverter<ArtistInfo, Artist> infoConverter = new ArtistInfoConverter();
            ApiResponse<ArtistInfo> response = ApiArtist.getInfo().withParams(LastFMParams.ARTIST.of(artist.getName())).execute();
            infoConverter.join(artist, response.getContent());
            return new ServiceResponse<>(artist, true);
        } catch (ApiResponseException | ApiRequestException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ServiceResponse<Artist> checkOutWith(Artist artist) throws ServiceException {
        try {
            Page<BaseSearchResult> resultPage = DiscogsDBApi.searchArtist(DiscogsDBParams.QUERY.of(artist.getName())).execute();
            artist.setDiscogsId(resultPage.getContent().get(0).getId());
            return new ServiceResponse<>(artist, true);
        } catch (DiscogsDBCallException | DiscogsDBErrorException e) {
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

}
