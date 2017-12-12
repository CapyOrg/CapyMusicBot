package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceContext;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Release;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by enableee on 11.12.17.
 */
public class ShowReleasesCommand extends BotCommand {
    private int phase;
    private int iterator;

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;

    public ShowReleasesCommand() {
        this.phase = FIRST_PHASE;
        this.iterator = 0;
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        StringBuilder messageBuilder = new StringBuilder();
        List<Artist> subscribes = MongoManager.getInstance().getSubscribesList(user.getId());
        if (phase == FIRST_PHASE) {
            messageBuilder
                    .append("Please, type the number of artist, ")
                    .append("releases of which you want to get.");
            sendMessageToUser(user, absSender, messageBuilder.toString());

            new ShowSubscribesListCommand().execute(absSender, user);

            phase = SECOND_PHASE;
            MongoManager.getInstance().addCommandToCommandsList(user.getId(), this);
        } else if (phase == SECOND_PHASE) {
            Service service = ServiceContext.getService();
            String userAnswer = getMessagesHistory().get(iterator);
            if ((userAnswer.matches("^[0-9]+$")) &&
                    (Integer.parseInt(userAnswer) > 0) &&
                    (Integer.parseInt(userAnswer) <= subscribes.size())) {
                String mbid = subscribes.get(Integer.parseInt(userAnswer) - 1).getMbid();
                String artistName = subscribes.get(Integer.parseInt(userAnswer) - 1).getName();
                List<Release> releases = new ArrayList<>();
                try {
                    releases = service
                            .getLastReleases(service
                                            .checkOutWith(new org.capy.musicbot.service.entries.Artist(artistName, mbid))
                                            .getContent(),
                                    Instant.now().minus(Period.ofDays(60)))
                            .getContent();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }

                if (!releases.isEmpty() || releases == null) {
                    for (Release release : releases) {
                        messageBuilder
                                .append(release.getTitle())
                                .append("\n");
                                if (release.getDate() != null)
                                    messageBuilder
                                            .append("Date of release: ")
                                            .append(release.getDate());
                                if (release.getDcType() != null)
                                    messageBuilder
                                            .append("Release type: ")
                                            .append(release.getDcType());
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
                        .append("Something went wrong. Please, check if the number you typed ")
                        .append("is valid and try again.");
                sendMessageToUser(user, absSender, messageBuilder.toString());
                MongoManager.getInstance().updateCommandState(user.getId(), this);
            }
        }
    }
}
