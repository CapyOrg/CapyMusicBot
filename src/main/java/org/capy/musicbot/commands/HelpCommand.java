package org.capy.musicbot.commands;

import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.bots.AbsSender;

import static org.capy.musicbot.BotHelper.sendMessageToUser;

/**
 * Created by enableee on 10.12.17.
 */
public class HelpCommand extends BotCommand {
    protected HelpCommand() {
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder
                .append("Available commands:\n")
                .append("/help - show the list of available commands\n")
                .append("/add - add an artist to your subscribes list\n")
                .append("/remove - remove the artist from your subscribes list\n")
                .append("/notifications_on - turn notifications mode on. That means ")
                .append("bot will message you about the latest news\n")
                .append("/notifications_off - turn notifications mode off\n")
                .append("/show_subscribes_list - show the list of your current subscribes\n")
                .append("/show_releases - show releases of an exact artist that you are subscribed on\n")
                .append("/show_releases_all - show releases of all artists that you are subscribed on\n")
                .append("/set_location - set location of events you want to get information about\n")
                .append("/show_events - show upcoming events of an exact artist in the city you've set by /set_location command");
        sendMessageToUser(user, absSender, messageBuilder.toString());
    }
}
