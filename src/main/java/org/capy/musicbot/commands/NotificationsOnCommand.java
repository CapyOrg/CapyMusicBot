package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by enableee on 10.12.17.
 */
public class NotificationsOnCommand extends BotCommand {
    public NotificationsOnCommand() {
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        long id = user.getId();
        MongoManager mongoManager = MongoManager.getInstance();
        StringBuilder messageBuilder = new StringBuilder();

        mongoManager.addUser(user);
        if (mongoManager.findUser(id) != null) {
            mongoManager.setUserNotificationsMode(id, true);
            messageBuilder.append("I successfully turned notifications mode on!");
        } else {
            messageBuilder.append("Oops! Something went wrong. Please, try again.");
        }
        sendMessageToUser(user, absSender, messageBuilder.toString());
    }
}
