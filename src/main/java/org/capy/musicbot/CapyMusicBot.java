package org.capy.musicbot;

import org.capy.musicbot.commands.BotCommand;
import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import static org.capy.musicbot.BotHelper.sendMessageToUser;
import static org.capy.musicbot.commands.CommandSimpleFactory.createCommand;


/**
 * Created by enableee on 10.12.17.
 */
public class CapyMusicBot extends TelegramLongPollingBot {
    private static CapyMusicBot instance;
    private static final Logger logger = LoggerFactory.getLogger(CapyMusicBot.class.getName());


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
                    if (command.execute(this, user, null))
                        logger.info("User @" + username + " started command " + command.getClass().getSimpleName());
                    else
                        logger.debug("User @" + username + " started command " + command.getClass().getSimpleName() + " and it failed");
                } catch (ServiceException e) {
                    logger.error("Method " + command.getClass().getSimpleName() + " started by user @" + username + " throws ", e);
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
                            if (user.getCurrentCommand().execute(this, user, null))
                                logger.info("User @" + username +
                                        " continued command " + user.getCurrentCommand().getClass().getSimpleName() +
                                        " with message \"" + messageText + "\"");
                            else
                                logger.debug("User @" + username +
                                        " continued command " + user.getCurrentCommand().getClass().getSimpleName() +
                                        " with message \"" + messageText + "\". Command failed");
                        } catch (ServiceException e) {
                            logger.error("Command " + command.getClass().getSimpleName() +
                                    " continued by user @" + username + " throws ", e);
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
