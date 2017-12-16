package org.capy.musicbot.service;

import org.capy.musicbot.service.converters.*;
import org.capy.musicbot.service.entries.Artist;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Location;
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
import ru.blizzed.openlastfm.OpenLastFMContext;
import ru.blizzed.openlastfm.methods.ApiArtist;
import ru.blizzed.openlastfm.methods.ApiResponse;
import ru.blizzed.openlastfm.methods.ApiUser;
import ru.blizzed.openlastfm.models.SearchResult;
import ru.blizzed.openlastfm.models.artist.ArtistInfo;
import ru.blizzed.openlastfm.models.artist.FoundArtist;
import ru.blizzed.openlastfm.params.LastFMParams;
import ru.blizzed.openlastfm.params.Period;
import ru.blizzed.opensongkick.ApiCallException;
import ru.blizzed.opensongkick.ApiErrorException;
import ru.blizzed.opensongkick.SongKickApi;
import ru.blizzed.opensongkick.params.SongKickParams;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
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
    public ServiceResponse<List<Release>> getLastReleases(Artist artist, Instant since) throws ServiceException {
        LocalDateTime date = LocalDateTime.ofInstant(since, ZoneId.systemDefault());
        try {
            List<Release> releases = fetchReleasesByYear(artist, date.getYear());
            ReleaseConverter releaseConverter = new ReleaseConverter(artist);
            Iterator<Release> iterator = releases.iterator();
            while (iterator.hasNext()) {
                Release release = iterator.next();
                releaseConverter.join(release, getReleaseInfo(release));
                if (release.getDate().isBefore(since)) iterator.remove();
            }
            return new ServiceResponse<>(releases, true);
        } catch (DiscogsDBCallException | DiscogsDBErrorException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ServiceResponse<List<Release>> getLastReleases(Artist artist, Release since) throws ServiceException {
        LocalDateTime date = LocalDateTime.ofInstant(since.getDate(), ZoneId.systemDefault());
        try {
            List<Release> releases = fetchReleasesByYear(artist, date.getYear());
            ReleaseConverter releaseConverter = new ReleaseConverter(artist);
            int ind = releases.indexOf(since);
            if (ind != -1) {
                releases = releases.stream().limit(ind/* > 1 ? ind - 1 : 0*/).collect(Collectors.toList());
            }

            for (Release release : releases) {
                releaseConverter.join(release, getReleaseInfo(release));
            }
            return new ServiceResponse<>(releases, true);
        } catch (DiscogsDBCallException | DiscogsDBErrorException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ServiceResponse<List<Event>> getEvents(Artist artist, Location location) throws ServiceException {
        EventConverter converter = new EventConverter();
        try {
            List<Event> locations = SongKickApi.eventSearch()
                    .byArtist(
                            artist.getName(),
                            SongKickParams.LOCATION_GEO.of(location.getLatitude(), location.getLongitude()),
                            SongKickParams.PER_PAGE.of(SEARCH_LIMIT)
                    )
                    .execute()
                    .getResults()
                    .stream()
                    .map(converter::convert)
                    .collect(Collectors.toList());
            return new ServiceResponse<>(locations, true);
        } catch (ApiCallException | ApiErrorException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ServiceResponse<List<Location>> findLocation(String query) throws ServiceException {
        LocationConverter converter = new LocationConverter();
        try {
            List<Location> locations = SongKickApi.locationSearch()
                    .byQuery(query, SongKickParams.PER_PAGE.of(SEARCH_LIMIT))
                    .execute()
                    .getResults()
                    .stream()
                    .map(converter::convert)
                    .collect(Collectors.toList());
            return new ServiceResponse<>(locations, true);
        } catch (ApiCallException | ApiErrorException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ServiceResponse<List<Artist>> getLastFmUserArtists(String lastFmUsername, int page, int perPage) throws ServiceException {
        try {
            ArtistConverter converter = new ArtistConverter();
            List<Artist> artists = ApiUser.getTopArtists().withParams(
                    LastFMParams.PERIOD.of(Period.TWELVE_MONTHS),
                    LastFMParams.USER.of(lastFmUsername),
                    LastFMParams.PAGE.of(page),
                    LastFMParams.LIMIT.of(perPage)
            ).execute()
                    .getContent()
                    .getItems()
                    .stream()
                    .map(converter::convert)
                    .filter(a -> !a.getMbid().isEmpty())
                    .collect(Collectors.toList());
            return new ServiceResponse<>(artists, true);
        } catch (ApiResponseException | ApiRequestException e) {
            throw new ServiceException(e);
        }
    }

    private List<Release> fetchReleasesByYear(Artist artist, int year) throws DiscogsDBCallException, DiscogsDBErrorException {
        ArtistReleaseConverter artistReleaseConverter = new ArtistReleaseConverter(artist);
        Page<ArtistRelease> releasesPage;
        List<Release> releases = new ArrayList<>();

        int currentPage = 1;
        do {
            releasesPage = getReleases(artist, currentPage);
            releases.addAll(releasesPage.getContent()
                    .stream()
                    .map(artistReleaseConverter::convert)
                    .filter(r -> r.getYear() >= year)
                    .collect(Collectors.toList())
            );
            currentPage++;
        } while (!releases.isEmpty()
                & releases.size() % releasesPage.getPagination().getPerPage() == 0
                & currentPage <= releasesPage.getPagination().getPagesCount());
        return releases;
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
        return DiscogsDBApi.getRelease(release.getMainId() == Release.UNKNOWN_ID ? release.getId() : release.getMainId()).execute();
    }

}