package org.capy.musicbot;

import org.capy.musicbot.commands.*;
import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;


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
                case "/add":
                    new AddCommand().execute(this, user);
                    break;
                case "/notifications_on":
                    new NotificationsOnCommand().execute(this, user);
                    break;
                case "/notifications_off":
                    new NotificationsOffCommand().execute(this, user);
                    break;
                case "/show_subscribes_list":
                    new ShowSubscribesListCommand().execute(this, user);
                    break;
                case "/remove":
                    new RemoveCommand().execute(this, user);
                    break;
                case "/show_releases":
                    new ShowReleasesCommand().execute(this, user);
                    break;
                default:
                    StringBuilder builder = new StringBuilder();
                    if (messageText.startsWith("/")) {
                        builder.append("I don't know this command. Please, use one of" +
                                "available commands.\n" +
                                "To see the list of available commands use /help");
                        try {
                            sendMessageToUser(chatId, builder.toString());
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        user = MongoManager.getInstance().findUser(id);
                        if (user != null) {
                            if (user.getCommands().isEmpty()) {

                                builder.append("Please, use any command to start working with bot.\n")
                                        .append("To see the list of available commands use /help");
                                try {
                                    sendMessageToUser(chatId, builder.toString());
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                user.getCurrentCommand().addMessage(messageText);
                                user.getCurrentCommand().execute(this, user);
                            }
                        } else {
                            builder.append("Please, use any command to start working with bot.\n")
                                    .append("To see the list of available commands use /help");
                            try {
                                sendMessageToUser(chatId, builder.toString());
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
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

    private void sendMessageToUser(long chatId, String text) throws TelegramApiException{
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId)
                .setText(text);
        execute(message);
    }
}
