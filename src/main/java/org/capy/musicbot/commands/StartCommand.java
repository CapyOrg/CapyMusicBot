package org.capy.musicbot.commands;

import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by enableee on 10.12.17.
 */
public class StartCommand extends BotCommand {
    public StartCommand() {
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        StringBuilder messageBuilder = new StringBuilder();
        String userName = user.getFirstName() + " " + user.getLastName();

        messageBuilder.append("Welcome, ")
                .append(userName)
                .append("!\n\n");
        messageBuilder.append("I am a CapyMusic bot.\n\n" +
                "I will help you to not miss news about " +
                "your favorite artists. To see the list " +
                "of available commands use /help");
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