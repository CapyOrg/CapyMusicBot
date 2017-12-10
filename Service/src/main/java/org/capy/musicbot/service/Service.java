package org.capy.musicbot.service;

import org.capy.musicbot.service.converters.ArtistInfoConverter;
import org.capy.musicbot.service.converters.ArtistReleaseConverter;
import org.capy.musicbot.service.converters.EntryConverter;
import org.capy.musicbot.service.converters.FoundArtistConverter;
import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Release;
import ru.blizzed.discogsdb.DiscogsDBApi;
import ru.blizzed.discogsdb.DiscogsDBCallException;
import ru.blizzed.discogsdb.DiscogsDBErrorException;
import ru.blizzed.discogsdb.model.Page;
import ru.blizzed.discogsdb.model.artist.ArtistRelease;
import ru.blizzed.discogsdb.model.search.BaseSearchResult;
import ru.blizzed.discogsdb.params.DiscogsDBParams;
import ru.blizzed.discogsdb.params.SortOrderParam;
import ru.blizzed.discogsdb.params.SortParam;
import ru.blizzed.openlastfm.ApiRequestException;
import ru.blizzed.openlastfm.ApiResponseException;
import ru.blizzed.openlastfm.methods.ApiArtist;
import ru.blizzed.openlastfm.methods.ApiResponse;
import ru.blizzed.openlastfm.models.SearchResult;
import ru.blizzed.openlastfm.models.artist.ArtistInfo;
import ru.blizzed.openlastfm.models.artist.FoundArtist;
import ru.blizzed.openlastfm.params.LastFMParams;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Service implements ServiceApi {

    private static final int SEARCH_LIMIT = 15;

    Service() {
    }

    public ServiceResponse<List<Artist>> findArtist(String name) throws ServiceException {
        try {
            SearchResult<FoundArtist> searchResult = ApiArtist.search().withParams(
                    LastFMParams.ARTIST.of(name),
                    LastFMParams.LIMIT.of(SEARCH_LIMIT)
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
            ApiResponse<ArtistInfo> response = ApiArtist.getInfo().withParams(LastFMParams.MBID.of(artist.getMbid())).execute();
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
            if (!resultPage.getContent().isEmpty()) artist.setDiscogsId(resultPage.getContent().get(0).getId());
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
    public ServiceResponse<List<Release>> getLastReleases(Artist artist, Instant since) throws ServiceException {
        ArtistReleaseConverter artistReleaseConverter = new ArtistReleaseConverter(artist);
        LocalDateTime date = LocalDateTime.ofInstant(since, ZoneId.systemDefault());
        try {
            Page<ArtistRelease> releasesPage;
            List<Release> releases = new ArrayList<>();

            int currentPage = 1;
            do {
                releasesPage = getReleases(artist, currentPage);
                releases.addAll(releasesPage.getContent()
                        .stream()
                        .map(artistReleaseConverter::convert)
                        .filter(r -> r.getYear() >= date.getYear())
                        .collect(Collectors.toList())
                );
                currentPage++;
            } while (!releases.isEmpty()
                    & releases.size() % releasesPage.getPagination().getPerPage() == 0
                    & currentPage <= releasesPage.getPagination().getPagesCount());

            return new ServiceResponse<>(releases, true);
        } catch (DiscogsDBCallException | DiscogsDBErrorException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ServiceResponse<List<Release>> getLastReleases(Artist artist, Release since) throws ServiceException {
        return getLastReleases(artist, since.getDate());
    }

    private Page<ArtistRelease> getReleases(Artist artist, int page) throws DiscogsDBErrorException, DiscogsDBCallException {
        return DiscogsDBApi.getArtistReleases(
                artist.getDiscogsId(),
                DiscogsDBParams.SORT.of(SortParam.Type.YEAR),
                DiscogsDBParams.SORT_ORDER.of(SortOrderParam.Type.DESC),
                DiscogsDBParams.PAGE.of(page)
        ).execute();
    }

    private ru.blizzed.discogsdb.model.release.Release getReleaseInfo(Release release) throws DiscogsDBErrorException, DiscogsDBCallException {
        return DiscogsDBApi.getRelease(release.getId()).execute();
    }

}
