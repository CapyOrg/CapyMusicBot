package org.capy.musicbot;

import org.capy.musicbot.database.DBConfig;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.notifier.Notifier;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Event;
import org.capy.musicbot.service.entries.Release;
import org.capy.musicbot.updater.UpdateService;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by enableee on 09.12.17.
 */
public class Main {

    public static void main(String[] args) throws ServiceException {
        /*ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        //load properties to get bot token and username
        try {
            BotConfig.loadProperties();
            DBConfig.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Register our bot
        try {
            botsApi.registerBot(new CapyMusicBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
*/
        UpdateService.with(new UpdateService.DataProvider() {
            @Override
            public List<Artist> getArtists() {
                return Collections.emptyList();
            }
        }).startTracking(new Notifier() {
            @Override
            public void notifyReleases(Artist artist, List<Release> releases) {

            }

            @Override
            public void notifyEvents(Artist artist, List<Event> events) {

            }
        });
    }
}
