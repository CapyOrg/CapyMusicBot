package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.mongodb.morphia.query.UpdateResults;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

import static org.capy.musicbot.BotHelper.*;
import static org.capy.musicbot.database.MongoManager.isQueryExecuted;

/**
 * Created by enableee on 11.12.17.
 */
public class RemoveCommand extends MultiphaseBotCommand {

    protected RemoveCommand() {
        super();
    }

    @Override
    public boolean execute(AbsSender absSender, User user, String[] args) {
        MongoManager mongoManager = MongoManager.getInstance();
        StringBuilder messageBuilder = new StringBuilder();
        List<Artist> subscribes = mongoManager.getUserSubscribesList(user.getId());
        switch (getCurrentPhase()) {
            case FIRST_PHASE:
                if (subscribes.size() != 0) {
                    ReplyKeyboardMarkup replyKeyboardMarkup = createKeyboardWithSubscribesList(subscribes);
                    setCurrentPhase(SECOND_PHASE);
                    isCommandExecuted &= isQueryExecuted(mongoManager.addCommandToCommandsList(user.getId(), this)) &&
                            sendMessageWithKeyboardToUser(user, absSender, "Please, choose the artist to remove", replyKeyboardMarkup);
                } else {
                    messageBuilder.append("You don't have any subscribes yet!");
                    isCommandExecuted &= isQueryExecuted(mongoManager.finishLastCommand(user.getId())) &&
                            sendMessageToUser(user, absSender, messageBuilder.toString());
                }
                break;
            case SECOND_PHASE:
                String userAnswer = getMessagesHistory().get(getIterator());
                String artistNumber = userAnswer.split("\\.")[0];
                if ((artistNumber.matches("^[0-9]+$")) &&
                        (Integer.parseInt(artistNumber) > 0) &&
                        (Integer.parseInt(artistNumber) <= subscribes.size())) {
                    String mbid = subscribes.get(Integer.parseInt(artistNumber) - 1).getMbid();
                    UpdateResults results = mongoManager.unsubscribeUser(user.getId(), mbid);
                    if (!mongoManager.isUserSubscribedOnArtist(user.getId(), mbid))
                        messageBuilder
                                .append("I successfully removed ")
                                .append(subscribes.get(Integer.parseInt(artistNumber) - 1).getName())
                                .append(" from your subscribes list!");
                    else
                        messageBuilder
                                .append("I could not delete ")
                                .append(subscribes.get(Integer.parseInt(artistNumber) - 1).getName())
                                .append(" from your subscribes list!");
                    isCommandExecuted &= isQueryExecuted(results) &&
                            sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), new ReplyKeyboardMarkup().setKeyboard(new ArrayList<>())) &&
                            isQueryExecuted(mongoManager.finishLastCommand(user.getId()));
                } else {
                    setIterator(getIterator() + 1);
                    messageBuilder
                            .append("Something went wrong. Please, try again.");
                    ReplyKeyboardMarkup replyKeyboardMarkup =
                            createKeyboardWithSubscribesList(MongoManager.getInstance().getUserSubscribesList(user.getId()));
                    isCommandExecuted &= isQueryExecuted(mongoManager.updateCommandState(user.getId(), this)) &&
                            sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup);
                }
                break;
            default:
                return false;
        }
        return isCommandExecuted;
    }
}
