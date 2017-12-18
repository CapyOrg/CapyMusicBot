package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceContext;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Release;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Period;
import java.util.List;

import static org.capy.musicbot.BotHelper.*;

/**
 * Created by enableee on 11.12.17.
 */
public class ShowReleasesCommand extends MultiphaseBotCommand {
    private static final int DAYS_AGO = 90;

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;

    protected ShowReleasesCommand() {
        super();
    }

    @Override
    public boolean execute(AbsSender absSender, User user, String[] args) throws ServiceException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        StringBuilder messageBuilder = new StringBuilder();
        MongoManager mongoManager = MongoManager.getInstance();
        List<Artist> subscribes = mongoManager.getUserSubscribesList(user.getId());
        ReplyKeyboardMarkup replyKeyboardMarkup;
        switch (getCurrentPhase()) {
            case FIRST_PHASE:
                messageBuilder
                        .append("Please, press at the button with the name of the artist, ")
                        .append("whose releases you want to get.");
                replyKeyboardMarkup = createKeyboardWithSubscribesList(subscribes);
                setCurrentPhase(SECOND_PHASE);
                return (queryIsExecuted(MongoManager.getInstance().addCommandToCommandsList(user.getId(), this)) &&
                        sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup));
            case SECOND_PHASE:
                Service service = ServiceContext.getService();
                String userAnswer = getMessagesHistory().get(getIterator());
                String artistNumber = userAnswer.split("\\.")[0];
                if ((artistNumber.matches("^[0-9]+$")) &&
                        (Integer.parseInt(artistNumber) > 0) &&
                        (Integer.parseInt(artistNumber) <= subscribes.size())) {
                    String mbid = subscribes.get(Integer.parseInt(artistNumber) - 1).getMbid();
                    String artistName = subscribes.get(Integer.parseInt(artistNumber) - 1).getName();
                    List<Release> releases = service
                            .getLastReleases(service
                                            .checkOutWith(new org.capy.musicbot.service.entries.Artist(artistName, mbid))
                                            .getContent(),
                                    Instant.now().minus(Period.of(0, 0, DAYS_AGO)))
                            .getContent();

                    if (!releases.isEmpty()) {
                        for (Release release : releases) {
                            messageBuilder
                                    .append(release.getTitle())
                                    .append("\n");
                            if (release.getDate() != null)
                                messageBuilder
                                        .append("Date of release: ")
                                        .append(formatter.format(Date.from(release.getDate())))
                                        .append("\n");
                            if (release.getTypes() != null) {
                                messageBuilder.append("Release type: ");
                                for (Release.Type type : release.getTypes())
                                    messageBuilder
                                            .append(type.name())
                                            .append(" ");
                            }
                            if (release.getImage() != null) {
                                SendPhoto photo = new SendPhoto()
                                        .setChatId(user.getChatId())
                                        .setPhoto(release.getImage())
                                        .setCaption(messageBuilder.toString());
                                try {
                                    absSender.sendPhoto(photo);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                return sendMessageToUser(user, absSender, messageBuilder.toString());
                            }
                            messageBuilder = new StringBuilder();
                        }
                    } else {
                        messageBuilder
                                .append("This artist has no new releases.");
                        sendMessageToUser(user, absSender, messageBuilder.toString());
                    }
                    return queryIsExecuted(mongoManager.finishLastCommand(user.getId()));
                } else {
                    setIterator(getIterator() + 1);
                    messageBuilder
                            .append("Something went wrong.\n")
                            .append("Please, press at the button with the name of the artist, ")
                            .append("releases of which you want to get.");
                    replyKeyboardMarkup = createKeyboardWithSubscribesList(subscribes);
                    return (queryIsExecuted(mongoManager.updateCommandState(user.getId(), this)) &&
                            sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup));
                }
            default:
                return false;
        }
    }

}
