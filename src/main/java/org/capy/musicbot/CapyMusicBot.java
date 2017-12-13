package org.capy.musicbot;

import org.capy.musicbot.commands.*;
import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import static org.capy.musicbot.BotHelper.sendMessageToUser;


/**
 * Created by enableee on 10.12.17.
 */
public class CapyMusicBot extends TelegramLongPollingBot {
    private static final String START_COMMAND = "/start";
    private static final String HELP_COMMAND = "/help";
    private static final String ADD_COMMAND = "/add";
    private static final String NOTIFICATIONS_OFF_COMMAND = "/notifications_off";
    private static final String NOTIFICATIONS_ON_COMMAND = "/notifications_on";
    private static final String REMOVE_COMMAND = "/remove";
    private static final String SHOW_RELEASES_COMMAND = "/show_releases";
    private static final String SHOW_RELEASES_ALL_COMMAND = "/show_releases_all";
    private static final String SHOW_SUBSCRIBES_LIST_COMMAND = "/show_subscribes_list";

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
                case START_COMMAND:
                    new StartCommand().execute(this, user);
                    break;
                case HELP_COMMAND:
                    new HelpCommand().execute(this, user);
                    break;
                case ADD_COMMAND:
                    new AddCommand().execute(this, user);
                    break;
                case NOTIFICATIONS_ON_COMMAND:
                    new NotificationsOnCommand().execute(this, user);
                    break;
                case NOTIFICATIONS_OFF_COMMAND:
                    new NotificationsOffCommand().execute(this, user);
                    break;
                case SHOW_SUBSCRIBES_LIST_COMMAND:
                    new ShowSubscribesListCommand().execute(this, user);
                    break;
                case REMOVE_COMMAND:
                    new RemoveCommand().execute(this, user);
                    break;
                case SHOW_RELEASES_COMMAND:
                    new ShowReleasesCommand().execute(this, user);
                    break;
                case SHOW_RELEASES_ALL_COMMAND:
                    new ShowReleasesAllCommand().execute(this, user);
                    break;
                default:
                    StringBuilder builder = new StringBuilder();
                    if (messageText.startsWith("/")) {
                        builder.append("I don't know this command. Please, use one of" +
                                "available commands.\n" +
                                "To see the list of available commands use /help");
                        sendMessageToUser(user, this, builder.toString());
                    } else {
                        user = MongoManager.getInstance().findUser(id);
                        if (user != null) {
                            if (user.getCommands().isEmpty()) {

                                builder.append("Please, use any command to start working with bot.\n")
                                        .append("To see the list of available commands use /help");
                                sendMessageToUser(user, this, builder.toString());
                            } else {
                                user.getCurrentCommand().addMessage(messageText);
                                user.getCurrentCommand().execute(this, user);
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
