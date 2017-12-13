package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceContext;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Release;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static org.capy.musicbot.BotHelper.sendMessageToUser;

/**
 * Created by enableee on 12.12.17.
 */
public class ShowReleasesAllCommand extends BotCommand {
    public ShowReleasesAllCommand() {
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        boolean hasNewReleases = false;
        Service service = ServiceContext.getService();
        StringBuilder messageBuilder = new StringBuilder();
        List<org.capy.musicbot.entities.Artist> subscribes = MongoManager.getInstance().getSubscribesList(user.getId());
        if (!subscribes.isEmpty()) {
            for (int i = 0; i < subscribes.size(); i++) {
                String artistName = subscribes.get(i).getName();
                String mbid = subscribes.get(i).getMbid();

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
                    messageBuilder.append(artistName);
                    sendMessageToUser(user, absSender, messageBuilder.toString());
                    messageBuilder = new StringBuilder();
                    hasNewReleases = true;
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
                                    .setCaption(messageBuilder.toString())
                                    .setPhoto(release.getImage());
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
                }
                messageBuilder = new StringBuilder();
            }
        } else if (subscribes.isEmpty() || !hasNewReleases) {
            messageBuilder
                    .append("All of artists that you are subscribed on ")
                    .append("don't have any new releases now.");
            sendMessageToUser(user, absSender, messageBuilder.toString());
        }
    }
}