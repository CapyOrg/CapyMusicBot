package org.capy.musicbot.updater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.capy.musicbot.Utils;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.notifier.Notifier;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceContext;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Release;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Service class that checks for artist's updates periodically
 *
 * @author BlizzedRu
 */
public final class UpdateService {

    private static final Logger log = LogManager.getLogger(UpdateService.class);

    public interface DataProvider {
        List<Artist> getArtists();
    }

    public enum Status {
        WORKING, STOPPED, WAITING
    }

    private static UpdateService instance;
    private DataProvider dataProvider;

    private Status status = Status.WAITING;

    private Timer timer;
    private Settings settings;

    private Notifier notifier;

    private UpdateService(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        settings = new Settings();
    }

    public static UpdateService with(DataProvider dataProvider) {
        return getInstance(dataProvider);
    }

    public void startTracking(Notifier notifier) {
        this.notifier = notifier;

        Instant lastUpdate = Instant.ofEpochSecond(settings.getLastUpdate());
        Instant updatePeriod = Instant.ofEpochSecond(settings.getUpdatePeriod());
        Instant nextUpdateTime = lastUpdate.plus(Duration.ofSeconds(updatePeriod.getEpochSecond()));

        if (nextUpdateTime.isBefore(Instant.now())) {
            startUpdate();
        } else {
            scheduleUpdate(Date.from(nextUpdateTime));
        }
    }

    public static void stopTracking() {
        if (instance != null &&instance.timer != null)
            instance.timer.cancel();
    }

    public static Status getStatus() {
        if (instance == null)
            return Status.STOPPED;
        return instance.status;
    }

    private void startUpdate() {
        stopTracking();
        timer = new Timer();
        timer.schedule(new UpdateTask(notifier), 10000);
        log.info("Start updating");
    }

    private void scheduleUpdate(Date when) {
        stopTracking();
        timer = new Timer();
        timer.schedule(new UpdateTask(notifier), when);
        log.info("Next update scheduled at {}", Utils.dateFormat(when));
    }

    private void setStatus(Status status) {
        this.status = status;
        log.info("Status changed to {}", status.name());
    }

    private Settings getSettings() {
        return this.settings;
    }

    private static UpdateService getInstance(DataProvider dataProvider) {
        if (instance == null)
            instance = new UpdateService(dataProvider);
        return instance;
    }

    private static class Settings {

        private Properties properties;
        private File propertiesFile;

        Settings() {
            properties = new Properties();
            try {
                propertiesFile = new File(getClass().getClassLoader().getResource("update_manager.properties").getFile());
                properties.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long getUpdatePeriod() {
            return Long.parseLong(properties.getProperty("updatePeriod"));
        }

        long getTimeOut() {
            return Long.parseLong(properties.getProperty("timeout"));
        }

        long getLastUpdate() {
            return Long.parseLong(properties.getProperty("lastUpdate"));
        }

        void setLastUpdate(long time) {
            try {
                properties.setProperty("lastUpdate", String.valueOf(time));
                properties.store(new FileOutputStream(propertiesFile), "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class UpdateTask extends TimerTask {

        private EventsUpdater eventsUpdater;
        private ReleasesUpdater releasesUpdater;

        private List<Artist> artists;

        UpdateTask(Notifier notifier) {
            Service service = ServiceContext.getService();
            eventsUpdater = new EventsUpdater(service, getSettings().getTimeOut());
            releasesUpdater = new ReleasesUpdater(service, getSettings().getTimeOut());

            eventsUpdater.addListener(new EventsListener(notifier));
            releasesUpdater.addListener(new ReleasesListener(notifier));

            artists = dataProvider.getArtists();
        }

        @Override
        public void run() {
            new Thread(() -> {
                try {
                    setStatus(Status.WORKING);
                    eventsUpdater.update(artists);
                    releasesUpdater.update(artists);
                    onSuccess();
                } catch (ServiceException e) {
                    e.printStackTrace();
                    log.error("Error while updating has occured {}", e.getMessage());
                    onError();
                }
            }).run();
        }

        private void onSuccess() {
            log.info("Successfully updated at {}", Utils.dateFormat(Instant.now()));
            getSettings().setLastUpdate(Instant.now().getEpochSecond());
            setStatus(Status.WAITING);
            scheduleUpdate(Date.from(Instant.now().plus(Duration.ofSeconds(settings.getUpdatePeriod()))));
        }

        private void onError() {
            setStatus(Status.STOPPED);
        }

        private class EventsListener implements EventsUpdater.Listener<Event, Artist> {

            private Notifier notifier;

            EventsListener(Notifier notifier) {
                this.notifier = notifier;
            }

            @Override
            public void onUpdatesReceived(Artist artist, List<Event> updates) {
                if (updates.isEmpty()) {
                    notifier.notifyEvents(artist, updates);
                }
            }
        }

        private class ReleasesListener implements EventsUpdater.Listener<Release, Artist> {

            private Notifier notifier;

            ReleasesListener(Notifier notifier) {
                this.notifier = notifier;
            }

            @Override
            public void onUpdatesReceived(Artist artist, List<Release> updates) {
                if (!updates.isEmpty()) {
                    artist.setLastRelease(updates.get(updates.size() - 1));
                    notifier.notifyReleases(artist, updates);
                }
            }
        }

    }

}
