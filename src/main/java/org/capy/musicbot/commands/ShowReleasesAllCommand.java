package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceContext;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Period;
import java.util.List;

import static org.capy.musicbot.BotHelper.sendMessageToUser;

/**
 * Created by enableee on 12.12.17.
 */
public class ShowReleasesAllCommand extends BotCommand {
    private static final Logger logger = LoggerFactory.getLogger(AddCommand.class.getSimpleName());
    private static final int DAYS_AGO = 90;

    protected ShowReleasesAllCommand() {

    }

    @Override
    public boolean execute(AbsSender absSender, User user, String[] args) throws ServiceException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        boolean hasNewReleases = false;
        Service service = ServiceContext.getService();
        StringBuilder messageBuilder = new StringBuilder();
        List<org.capy.musicbot.entities.Artist> subscribes = MongoManager.getInstance().getUserSubscribesList(user.getId());
        if (!subscribes.isEmpty()) {
            for (int i = 0; i < subscribes.size(); i++) {
                String artistName = subscribes.get(i).getName();
                String mbid = subscribes.get(i).getMbid();

                List<Release> releases = service
                        .getLastReleases(service
                                        .checkOutWith(new org.capy.musicbot.service.entries.Artist(artistName, mbid))
                                        .getContent(),
                                Instant.now().minus(Period.of(0, 0, DAYS_AGO)))
                        .getContent();


                if ((releases != null) && (!releases.isEmpty())) {
                    messageBuilder.append(artistName);
                    isCommandExecuted &= sendMessageToUser(user, absSender, messageBuilder.toString());
                    messageBuilder = new StringBuilder();
                    hasNewReleases = true;
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
                                    .setCaption(messageBuilder.toString())
                                    .setPhoto(release.getImage());
                            try {
                                absSender.sendPhoto(photo);
                            } catch (TelegramApiException e) {
                                logger.error("Failed to send photo message in command " +
                                        ShowReleasesAllCommand.class.getSimpleName() +
                                        "; user @" + user.getUsername(), e);
                            }
                        } else {
                            isCommandExecuted &= sendMessageToUser(user, absSender, messageBuilder.toString());
                        }
                        messageBuilder = new StringBuilder();
                    }
                }
                messageBuilder = new StringBuilder();
            }
        }
        if (subscribes.isEmpty() || !hasNewReleases) {
            messageBuilder
                    .append("All of artists that you are subscribed on ")
                    .append("don't have any new releases now.");
            isCommandExecuted &= sendMessageToUser(user, absSender, messageBuilder.toString());
        }
        return isCommandExecuted;
    }
}