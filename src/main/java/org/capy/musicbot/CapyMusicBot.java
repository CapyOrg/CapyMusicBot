package org.capy.musicbot;

import org.capy.musicbot.commands.BotCommand;
import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.ServiceException;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import static org.capy.musicbot.BotHelper.sendMessageToUser;
import static org.capy.musicbot.commands.CommandSimpleFactory.createCommand;


/**
 * Created by enableee on 10.12.17.
 */
public class CapyMusicBot extends TelegramLongPollingBot {
    private static CapyMusicBot instance;

    public static CapyMusicBot getInstance() {
        final CapyMusicBot currentInstance;
        if (instance == null)
            instance = new CapyMusicBot();
        currentInstance = instance;
        return currentInstance;
    }

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

            BotCommand command = createCommand(messageText);
            if (command != null) {
                try {
                    command.execute(this, user, null);
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }

            else {
                StringBuilder builder = new StringBuilder();
                if (messageText.startsWith("/")) {
                    builder.append("I don't know this command. Please, use one of" +
                            "available commands.\n" +
                            "To see the list of available commands use /help");
                    sendMessageToUser(user, this, builder.toString());
                } else {
                    user = MongoManager.getInstance().findUser(id);
                    if (user != null && !user.getCommands().isEmpty()) {
                        user.getCurrentCommand().addMessage(messageText);
                        try {
                            user.getCurrentCommand().execute(this, user, null);
                        } catch (ServiceException e) {
                            e.printStackTrace();
                        }

                    } else {
                        builder.append("Please, use any command to start working with bot.\n")
                                .append("To see the list of available commands use /help");
                        sendMessageToUser(user, this, builder.toString());
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
}
