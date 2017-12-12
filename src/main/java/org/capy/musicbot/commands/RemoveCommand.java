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
public class RemoveCommand extends BotCommand {
    private int phase;
    private int iterator;

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;

    public RemoveCommand() {
        this.phase = FIRST_PHASE;
        this.iterator = 0;
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        StringBuilder messageBuilder = new StringBuilder();
        List<Artist> subscribes = MongoManager.getInstance().getSubscribesList(user.getId());
        if (phase == FIRST_PHASE) {
            messageBuilder
                    .append("Please, type the number of artist you want to remove ")
                    .append("from your subscribes list.\n");
            sendMessageToUser(user, absSender, messageBuilder.toString());

            new ShowSubscribesListCommand().execute(absSender, user);

            phase = SECOND_PHASE;
            MongoManager.getInstance().addCommandToCommandsList(user.getId(), this);
        } else if (phase == SECOND_PHASE) {
            String userAnswer = getMessagesHistory().get(iterator);
            if ((userAnswer.matches("^[0-9]+$")) &&
                    (Integer.parseInt(userAnswer) > 0) &&
                    (Integer.parseInt(userAnswer) <= subscribes.size())) {
                String mbid = subscribes.get(Integer.parseInt(userAnswer) - 1).getMbid();
                MongoManager.getInstance().unsubscribeUser(user.getId(), mbid);
                if (!MongoManager.getInstance().isUserSubscribedOnArtist(user.getId(), mbid))
                    messageBuilder
                            .append("I successfully removed ")
                            .append(subscribes.get(Integer.parseInt(userAnswer) - 1).getName())
                            .append(" from your subscribes list!");
                else
                    messageBuilder
                            .append("I could not delete ")
                            .append(subscribes.get(Integer.parseInt(userAnswer) - 1).getName())
                            .append(" from your subscribes list!");
                sendMessageToUser(user, absSender, messageBuilder.toString());
                MongoManager.getInstance().finishLastCommand(user.getId());
            } else {
                iterator++;
                messageBuilder
                        .append("Something went wrong. Please, check if the number you typed ")
                        .append("is valid and try again.");
                sendMessageToUser(user, absSender, messageBuilder.toString());
                MongoManager.getInstance().updateCommandState(user.getId(), this);
            }
        }

    }
}
