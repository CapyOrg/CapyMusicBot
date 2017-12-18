package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceContext;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Event;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.AbsSender;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.capy.musicbot.BotHelper.*;

/**
 * Created by enableee on 14.12.17.
 */
public class ShowEventsCommand extends MultiphaseBotCommand {

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;

    protected ShowEventsCommand() {
        super();
    }

    @Override
    public boolean execute(AbsSender absSender, User user, String[] args) throws ServiceException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        StringBuilder messageBuilder = new StringBuilder();
        MongoManager mongoManager = MongoManager.getInstance();
        List<Artist> subscribes = mongoManager.getUserSubscribesList(user.getId());
        switch (getCurrentPhase()) {
            case FIRST_PHASE:
                if (mongoManager.findUser(user.getId()).getLocation() != null) {
                    messageBuilder
                            .append("Please, press at the button with the name of the artist, ")
                            .append("whose events you want to get.");

                    ReplyKeyboardMarkup replyKeyboardMarkup;
                    replyKeyboardMarkup = createKeyboardWithSubscribesList(subscribes);
                    setCurrentPhase(SECOND_PHASE);
                    return (queryIsExecuted(mongoManager.addCommandToCommandsList(user.getId(), this)) &&
                            sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup));
                } else {
                    messageBuilder
                            .append("You should firstly set your location by using /set_location command");
                    return sendMessageToUser(user, absSender, messageBuilder.toString());
                }
            case SECOND_PHASE:
                Service service = ServiceContext.getService();
                String userAnswer = getMessagesHistory().get(getIterator());
                String artistNumber = userAnswer.split("\\.")[0];
                if ((artistNumber.matches("^[0-9]+$")) &&
                        (Integer.parseInt(artistNumber) > 0) &&
                        (Integer.parseInt(artistNumber) <= subscribes.size())) {
                    String mbid = subscribes.get(Integer.parseInt(artistNumber) - 1).getMbid();
                    String artistName = subscribes.get(Integer.parseInt(artistNumber) - 1).getName();
                    List<Event> events = service
                            .getEvents(service
                                            .checkOutWith(new org.capy.musicbot.service.entries.Artist(artistName, mbid))
                                            .getContent(),
                                    mongoManager.findUser(user.getId()).getLocation()).getContent();

                    if (!events.isEmpty()) {
                        for (Event event : events) {
                            messageBuilder
                                    .append(event.getName())
                                    .append("\n")
                                    .append(formatter.format(Date.from(event.getDate())))
                                    .append("\n")
                                    .append(event.getUri());
                            sendMessageToUser(user, absSender, messageBuilder.toString());
                            messageBuilder = new StringBuilder();
                            user.addShownEvent(mbid, event.getId());
                            mongoManager.updateUserState(user);
                        }
                    } else {
                        messageBuilder
                                .append("This artist has no events in the near future.");
                        sendMessageToUser(user, absSender, messageBuilder.toString());
                    }
                    return (queryIsExecuted(mongoManager.finishLastCommand(user.getId())));
                } else {
                    setIterator(getIterator() + 1);
                    messageBuilder
                            .append("Something went wrong.\n")
                            .append("Please, press at the button with the name of the artist, ")
                            .append("releases of which you want to get.");
                    ReplyKeyboardMarkup replyKeyboardMarkup;
                    replyKeyboardMarkup = createKeyboardWithSubscribesList(subscribes);
                    return (queryIsExecuted(mongoManager.updateCommandState(user.getId(), this)) &&
                            sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup));
                }
            default:
                return false;
        }
    }

}
