package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.bots.AbsSender;

import static org.capy.musicbot.BotHelper.sendMessageToUser;
import static org.capy.musicbot.database.MongoManager.isQueryExecuted;

/**
 * Created by enableee on 10.12.17.
 */
public class NotificationsOnCommand extends BotCommand {
    protected NotificationsOnCommand() {
    }

    @Override
    public boolean execute(AbsSender absSender, User user, String[] args) {
        long id = user.getId();
        MongoManager mongoManager = MongoManager.getInstance();
        StringBuilder messageBuilder = new StringBuilder();

        mongoManager.addUser(user);
        if (mongoManager.findUser(id) != null) {
            messageBuilder.append("I successfully turned notifications mode on!");
            isCommandExecuted &= isQueryExecuted(mongoManager.setUserNotificationsMode(id, true)) &&
                    sendMessageToUser(user, absSender, messageBuilder.toString());
        } else {
            messageBuilder.append("Oops! Something went wrong. Please, try again.");
            isCommandExecuted &= sendMessageToUser(user, absSender, messageBuilder.toString());
        }
        return isCommandExecuted;
    }
}
