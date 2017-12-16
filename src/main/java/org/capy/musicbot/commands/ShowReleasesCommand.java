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
import java.util.ArrayList;
import java.util.List;

import static org.capy.musicbot.BotHelper.*;

/**
 * Created by enableee on 11.12.17.
 */
public class ShowReleasesCommand extends BotCommand {
    private static final int DAYS_AGO = 90;
    private int phase;
    private int iterator;

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;

    protected ShowReleasesCommand() {
        this.phase = FIRST_PHASE;
        this.iterator = 0;
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        StringBuilder messageBuilder = new StringBuilder();
        List<Artist> subscribes = MongoManager.getInstance().getUserSubscribesList(user.getId());
        if (phase == FIRST_PHASE) {
            messageBuilder
                    .append("Please, press at the button with the name of the artist, ")
                    .append("whose releases you want to get.");

            ReplyKeyboardMarkup replyKeyboardMarkup;
            replyKeyboardMarkup = createKeyboardWithSubscribesList(subscribes);
            sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup);
            phase = SECOND_PHASE;
            MongoManager.getInstance().addCommandToCommandsList(user.getId(), this);
        } else if (phase == SECOND_PHASE) {
            Service service = ServiceContext.getService();
            String userAnswer = getMessagesHistory().get(iterator);
            String artistNumber = userAnswer.split("\\.")[0];
            if ((artistNumber.matches("^[0-9]+$")) &&
                    (Integer.parseInt(artistNumber) > 0) &&
                    (Integer.parseInt(artistNumber) <= subscribes.size())) {
                String mbid = subscribes.get(Integer.parseInt(artistNumber) - 1).getMbid();
                String artistName = subscribes.get(Integer.parseInt(artistNumber) - 1).getName();
                List<Release> releases = new ArrayList<>();
                try {
                    releases = service
                            .getLastReleases(service
                                            .checkOutWith(new org.capy.musicbot.service.entries.Artist(artistName, mbid))
                                            .getContent(),
                                    Instant.now().minus(Period.of(0, 0, DAYS_AGO)))
                            .getContent();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }

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
                            sendMessageToUser(user, absSender, messageBuilder.toString());
                        }
                        messageBuilder = new StringBuilder();
                    }
                } else {
                    messageBuilder
                            .append("This artist has no new releases.");
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
