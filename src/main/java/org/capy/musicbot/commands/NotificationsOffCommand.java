package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.bots.AbsSender;

import static org.capy.musicbot.BotHelper.sendMessageToUser;
import static org.capy.musicbot.database.MongoManager.isQueryExecuted;

/**
 * Created by enableee on 10.12.17.
 */
public class NotificationsOffCommand extends BotCommand {
    protected NotificationsOffCommand() {
    }

    @Override
    public boolean execute(AbsSender absSender, User user, String[] args) {
        long id = user.getId();
        StringBuilder messageBuilder = new StringBuilder();
        MongoManager mongoManager = MongoManager.getInstance();

        mongoManager.addUser(user);
        if (mongoManager.findUser(id) != null) {
            messageBuilder.append("I successfully turned notifications mode off!");
            isCommandExecuted &= isQueryExecuted(mongoManager.setUserNotificationsMode(id, false)) &&
                    sendMessageToUser(user, absSender, messageBuilder.toString());
        } else {
            messageBuilder.append("Oops! Something went wrong. Please, try again.");
            isCommandExecuted &= sendMessageToUser(user, absSender, messageBuilder.toString());
        }
        return isCommandExecuted;
    }
}
