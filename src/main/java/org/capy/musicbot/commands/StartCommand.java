package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.bots.AbsSender;

import static org.capy.musicbot.BotHelper.sendMessageToUser;

/**
 * Created by enableee on 10.12.17.
 */
public class StartCommand extends BotCommand {
    protected StartCommand() {
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        StringBuilder messageBuilder = new StringBuilder();
        String userName = user.getFirstName() + " " + user.getLastName();

        messageBuilder.append("Welcome, ")
                .append(userName)
                .append("!\n\n");
        messageBuilder
                .append("I am a CapyMusic bot.\n\n")
                .append("I will help you to not miss news about ")
                .append("your favorite artists. To see the list ")
                .append("of available commands use /help");
        sendMessageToUser(user, absSender, messageBuilder.toString());
        MongoManager.getInstance().addUser(user);
    }
}
