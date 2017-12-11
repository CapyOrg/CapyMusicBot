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
        messageBuilder.append("Available commands:\n" +
                "/help - show the list of available commands\n" +
                "/add - add an artist to your subscribes list\n" +
                "/notifications_on - turn notifications mode on. That means " +
                "bot will message you about the latest news.\n" +
                "/notifications_off - turn notifications mode off.");
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
