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
import java.util.ArrayList;
import java.util.List;

import static org.capy.musicbot.BotHelper.*;

/**
 * Created by enableee on 14.12.17.
 */
public class ShowEventsCommand extends BotCommand {
    private int phase;
    private int iterator;

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;

    public ShowEventsCommand() {
        this.phase = FIRST_PHASE;
        iterator = 0;
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        StringBuilder messageBuilder = new StringBuilder();
        MongoManager mongoManager = MongoManager.getInstance();
        List<Artist> subscribes = mongoManager.getSubscribesList(user.getId());
        if (phase == FIRST_PHASE) {
            messageBuilder
                    .append("Please, press at the button with the name of the artist, ")
                    .append("whose events you want to get.");

            ReplyKeyboardMarkup replyKeyboardMarkup;
            replyKeyboardMarkup = createKeyboardWithSubscribesList(subscribes);
            sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup);
            phase = SECOND_PHASE;
            mongoManager.addCommandToCommandsList(user.getId(), this);
        } else if (phase == SECOND_PHASE) {
            Service service = ServiceContext.getService();
            String userAnswer = getMessagesHistory().get(iterator);
            String artistNumber = userAnswer.split("\\.")[0];
            if ((artistNumber.matches("^[0-9]+$")) &&
                    (Integer.parseInt(artistNumber) > 0) &&
                    (Integer.parseInt(artistNumber) <= subscribes.size())) {
                String mbid = subscribes.get(Integer.parseInt(artistNumber) - 1).getMbid();
                String artistName = subscribes.get(Integer.parseInt(artistNumber) - 1).getName();
                List<Event> events = new ArrayList<>();
                try {
                    events = service
                            .getEvents(service
                                            .checkOutWith(new org.capy.musicbot.service.entries.Artist(artistName, mbid))
                                            .getContent(),
                                    mongoManager.findUser(user.getId()).getLocation())
                            .getContent();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }

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
                    }
                } else {
                    messageBuilder
                            .append("This artist has no events in the near future.");
                    sendMessageToUser(user, absSender, messageBuilder.toString());
                }
                MongoManager.getInstance().finishLastCommand(user.getId());
            } else {
                iterator++;
                messageBuilder
                        .append("Something went wrong.\n")
                        .append("Please, press at the button with the name of the artist, ")
                        .append("releases of which you want to get.");
                ReplyKeyboardMarkup replyKeyboardMarkup;
                replyKeyboardMarkup = createKeyboardWithSubscribesList(subscribes);
                sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup);
                MongoManager.getInstance().updateCommandState(user.getId(), this);
            }
        }
    }
}
