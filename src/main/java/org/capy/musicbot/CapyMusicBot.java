package org.capy.musicbot;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.List;

/**
 * Created by enableee on 10.12.17.
 */
public class CapyMusicBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        //stub
    }

    @Override
    public String getBotUsername() {
        return BotConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return BotConfig.getBotToken();
    }
}
