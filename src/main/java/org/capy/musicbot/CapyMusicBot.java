package org.capy.musicbot;

import org.capy.musicbot.commands.HelpCommand;
import org.capy.musicbot.commands.StartCommand;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.List;

/**
 * Created by enableee on 10.12.17.
 */
public class CapyMusicBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            //getting user's data to create a User object
            String firstName = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();
            String username = update.getMessage().getChat().getUserName();
            long id = update.getMessage().getChat().getId();
            User user = new User(id, chatId, username, firstName, lastName);

            switch (messageText) {
                case "/start":
                    new StartCommand().execute(this, user);
                    break;
                case "/help":
                    new HelpCommand().execute(this, user);
                    break;
                default:
                    //stub
            }
        }
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
