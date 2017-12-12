package org.capy.musicbot.commands;

import org.capy.musicbot.entities.User;
import org.mongodb.morphia.annotations.Embedded;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enableee on 10.12.17.
 */
@Embedded
public abstract class BotCommand {
    //a list of user's messages that are associated with this exact instance of BotCommand
    private List<String> messagesHistory = new ArrayList<>();

    public BotCommand() {
    }

    public BotCommand(List<String> messages) {
        messagesHistory = messages;
    }

    public abstract void execute(AbsSender absSender, User user);

    public void addMessage(String message) {
        messagesHistory.add(message);
    }

    public void sendMessageToUser(User user, AbsSender absSender, String text) {
        SendMessage message = new SendMessage()
                .setChatId(user.getChatId())
                .setText(text);
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public List<String> getMessagesHistory() {
        return messagesHistory;
    }
}
