package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;

/**
 * Created by enableee on 11.12.17.
 */
public class ShowSubscribesListCommand extends BotCommand {
    public ShowSubscribesListCommand() {
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        StringBuilder messageBuilder = new StringBuilder();
        List<Artist> subscribes = MongoManager.getInstance().getSubscribesList(user.getId());
        int i = 0;
        if (subscribes.size() != 0) {
            for (Artist artist : subscribes) {
                messageBuilder
                        .append(Integer.toString(++i))
                        .append(". ")
                        .append(artist.getName())
                        .append("\n");
            }
        } else
            messageBuilder.append("You don't have any subscribes yet!");

        sendMessageToUser(user, absSender, messageBuilder.toString());
    }
}
