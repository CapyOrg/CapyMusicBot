package org.capy.musicbot.commands;

import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.ServiceException;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.query.UpdateResults;
import org.telegram.telegrambots.bots.AbsSender;

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

    public abstract boolean execute(AbsSender absSender, User user, String[] args) throws ServiceException;

    public void addMessage(String message) {
        messagesHistory.add(message);
    }

    public List<String> getMessagesHistory() {
        return messagesHistory;
    }

    protected static boolean queryIsExecuted(UpdateResults results) {
        return (results.getInsertedCount() != 0 || results.getUpdatedCount() != 0);
    }
}
