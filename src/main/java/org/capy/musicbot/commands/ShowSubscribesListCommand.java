package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.List;

import static org.capy.musicbot.BotHelper.sendMessageToUser;

/**
 * Created by enableee on 11.12.17.
 */
public class ShowSubscribesListCommand extends BotCommand {
    protected ShowSubscribesListCommand() {
    }

    @Override
    public boolean execute(AbsSender absSender, User user, String[] args) {
        StringBuilder messageBuilder = new StringBuilder();
        List<Artist> subscribes = MongoManager.getInstance().getUserSubscribesList(user.getId());
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
        return true;
    }
}
