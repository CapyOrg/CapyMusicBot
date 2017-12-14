package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

import static org.capy.musicbot.BotHelper.sendMessageToUser;
import static org.capy.musicbot.BotHelper.sendMessageWithKeyboardToUser;
import static org.capy.musicbot.BotHelper.createKeyboardWithSubscribesList;

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
            ReplyKeyboardMarkup replyKeyboardMarkup;
            if (subscribes.size() != 0) {
                replyKeyboardMarkup = createKeyboardWithSubscribesList(subscribes);
                sendMessageWithKeyboardToUser(user, absSender, "Please, choose the artist to remove", replyKeyboardMarkup);
                phase = SECOND_PHASE;
                MongoManager.getInstance().addCommandToCommandsList(user.getId(), this);
            } else {
                messageBuilder.append("You don't have any subscribes yet!");
                sendMessageToUser(user, absSender, messageBuilder.toString());
                MongoManager.getInstance().finishLastCommand(user.getId());
            }
        } else if (phase == SECOND_PHASE) {
            String userAnswer = getMessagesHistory().get(iterator);
            String artistNumber = userAnswer.split("\\.")[0];
            if ((artistNumber.matches("^[0-9]+$")) &&
                    (Integer.parseInt(artistNumber) > 0) &&
                    (Integer.parseInt(artistNumber) <= subscribes.size())) {
                String mbid = subscribes.get(Integer.parseInt(artistNumber) - 1).getMbid();
                MongoManager.getInstance().unsubscribeUser(user.getId(), mbid);
                if (!MongoManager.getInstance().isUserSubscribedOnArtist(user.getId(), mbid))
                    messageBuilder
                            .append("I successfully removed ")
                            .append(subscribes.get(Integer.parseInt(artistNumber) - 1).getName())
                            .append(" from your subscribes list!");
                else
                    messageBuilder
                            .append("I could not delete ")
                            .append(subscribes.get(Integer.parseInt(artistNumber) - 1).getName())
                            .append(" from your subscribes list!");
                sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), new ReplyKeyboardMarkup().setKeyboard(new ArrayList<KeyboardRow>()));
                MongoManager.getInstance().finishLastCommand(user.getId());
            } else {
                iterator++;
                messageBuilder
                        .append("Something went wrong. Please, try again.");
                ReplyKeyboardMarkup replyKeyboardMarkup =
                        createKeyboardWithSubscribesList(MongoManager.getInstance().getSubscribesList(user.getId()));
                sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup);
                MongoManager.getInstance().updateCommandState(user.getId(), this);
            }
        }

    }


}
