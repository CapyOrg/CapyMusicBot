package org.capy.musicbot;

import org.capy.musicbot.database.DBConfig;
import org.capy.musicbot.service.ServiceException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;

/**
 * Created by enableee on 09.12.17.
 */
public class Main {

    public static void main(String[] args) throws ServiceException {
        ApiContextInitializer.init();
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
    }
}
