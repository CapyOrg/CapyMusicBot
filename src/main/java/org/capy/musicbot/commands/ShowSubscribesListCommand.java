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
        if (subscribes.size() != 0)
            subscribes
                    .stream()
                    .forEach(artist -> messageBuilder
                            .append(artist.getName())
                            .append("\n"));
        else
            messageBuilder.append("You don't have any subscribes yet!");

        SendMessage message = new SendMessage()
                .setChatId(user.getChatId())
                .setText(messageBuilder.toString());

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
