package org.capy.musicbot.notifier;

import org.capy.musicbot.CapyMusicBot;
import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Release;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.capy.musicbot.BotHelper.sendMessageToUser;

/**
 * Created by enableee on 15.12.17.
 */
public class BaseNotifier implements Notifier {
    @Override
    public void notifyReleases(Artist artist, List<Release> releases) {
        //stub
    }

    @Override
    public void notifyEvents(Artist artist, List<Event> events) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        MongoManager mongoManager = MongoManager.getInstance();
        List<User> subscribers = mongoManager.getArtistSubscribersList(artist.getMbid());
        for (User subscriber : subscribers) {
            for (Event event : events) {
                if (subscriber.getLastShownEvents().get(artist.getMbid()) != event.getId()) {
                    StringBuilder messageBuilder = new StringBuilder()
                            .append(event.getName())
                            .append("\n")
                            .append(formatter.format(Date.from(event.getDate())))
                            .append("\n")
                            .append(event.getUri());
                    sendMessageToUser(subscriber, CapyMusicBot.getInstance(), messageBuilder.toString());
                }
            }
        }
    }
}
