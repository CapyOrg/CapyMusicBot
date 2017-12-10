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
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            //getting user's data to create a User object
            String first_name = update.getMessage().getChat().getFirstName();
            String last_name = update.getMessage().getChat().getLastName();
            String username = update.getMessage().getChat().getUserName();
            long id = update.getMessage().getChat().getId();
            User user = new User(id, chat_id, username, first_name, last_name);

            switch (message_text) {
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
