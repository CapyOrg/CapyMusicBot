package org.capy.musicbot.commands;

import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by enableee on 10.12.17.
 */
public class HelpCommand extends BotCommand {
    public HelpCommand() {
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
                .append("/show_releases_all - show releases of all artists that you are subscribed on");
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(user.getChatId())
                .setText(messageBuilder.toString());

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
