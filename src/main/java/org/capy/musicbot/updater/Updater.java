package org.capy.musicbot.updater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract class for updater of some stuffs like releases, events, etc
 *
 * @param <UpdateType> type of update
 * @param <Source> type of source (whose updates needed)
 * @author BlizzedRu
 */
public abstract class Updater<UpdateType, Source> {

    private static final Logger log = LogManager.getLogger(Updater.class);


    public interface Listener<UpdateType, Source> {
        void onUpdatesReceived(Source source, List<UpdateType> updates);
    }

    private long timeout;
    protected Service service;

    public Updater(Service service, long timeout) {
        this.timeout = timeout;
        this.service = service;
        listeners = new ArrayList<>();
    }

    private List<Listener<UpdateType, Source>> listeners;

    public void update(List<Source> sources) throws ServiceException {
        for (Source source : sources) {
            List<UpdateType> updates = getUpdates(source);
            if (!updates.isEmpty()) notifyListeners(source, updates);
            takeTimeout();
        }
    }

    public void addListener(Listener<UpdateType, Source> listener) {
        if (listener != null)
            listeners.add(listener);
    }

    public void removeListener(Listener<UpdateType, Source> listener) {
        if (listener != null && listeners.contains(listener))
            listeners.remove(listener);
    }

    protected abstract List<UpdateType> getUpdates(Source source) throws ServiceException;

    protected void takeTimeout() {
        try {
            log.debug("Taking timeout");
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            log.error("An error occurred while taking timeout: {}", ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void notifyListeners(Source source, final List<UpdateType> updates) {
        listeners.stream()
                .filter(Objects::nonNull)
                .forEach(listener -> listener.onUpdatesReceived(source, updates));
    }

}
