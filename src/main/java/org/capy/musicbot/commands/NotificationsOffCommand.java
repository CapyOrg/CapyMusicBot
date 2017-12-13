package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by enableee on 10.12.17.
 */
public class NotificationsOffCommand extends BotCommand {
    public NotificationsOffCommand() {
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        long id = user.getId();
        StringBuilder messageBuilder = new StringBuilder();
        MongoManager mongoManager = MongoManager.getInstance();

        mongoManager.addUser(user);
        if (mongoManager.findUser(id) != null) {
            mongoManager.setUserNotificationsMode(id, false);
            messageBuilder.append("I successfully turned notifications mode off!");
        } else {
            messageBuilder.append("Oops! Something went wrong. Please, try again.");
        }
        sendMessageToUser(user, absSender, messageBuilder.toString());
    }
}
