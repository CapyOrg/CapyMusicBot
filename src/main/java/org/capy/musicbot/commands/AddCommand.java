package org.capy.musicbot.commands;

import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceContext;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Artist;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.capy.musicbot.BotHelper.*;

/**
 * Created by enableee on 10.12.17.
 */
public class AddCommand extends BotCommand {
    private int phase;
    private int iterator;
    private int iteratorMaxValue;

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;
    private final static int THIRD_PHASE = 3;

    protected AddCommand() {
        this.phase = FIRST_PHASE;
        this.iterator = 0;
        this.iteratorMaxValue = -1;
    }

    public void execute(AbsSender absSender, User user) {
        StringBuilder messageBuilder = new StringBuilder();
        MongoManager mongoManager = MongoManager.getInstance();
        Service service = ServiceContext.getService();

        if (phase == FIRST_PHASE) {
            messageBuilder
                    .append("Please, type in the name of the artist you want to add to your subscribes list:");
            sendMessageToUser(user, absSender, messageBuilder.toString());
            phase = SECOND_PHASE;
            mongoManager.addCommandToCommandsList(user.getId(), this);
        } else if (phase == SECOND_PHASE) {
            String artistName = getMessagesHistory().get(0);
            List<Artist> artists = getArtistsOfferList(artistName);

            //max number of artists that bot could offer to user
            iteratorMaxValue = artists.size() - 1;
            if (iterator <= iteratorMaxValue) {
                SendPhoto photoMessage = createArtistOfferMessage(user, artists, iterator);
                String[] yesOrNo = {"Yes", "No"};
                ReplyKeyboardMarkup replyKeyboardMarkup = createKeyboard(new ArrayList<String>(Arrays.asList(yesOrNo)), true, true);
                try {
                    absSender.sendPhoto(photoMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                sendMessageWithKeyboardToUser(user, absSender, "Please, press \"yes\" or \"no\"", replyKeyboardMarkup);
                phase = THIRD_PHASE;
                mongoManager.updateCommandState(user.getId(), this);
            } else {
                messageBuilder
                        .append("Oops! I can't find artist you told me about. ")
                        .append("Perhaps you typed artist's name wrong or this artist isn't ")
                        .append("popular enough. :<");
                sendMessageToUser(user, absSender, messageBuilder.toString());
                mongoManager.finishLastCommand(user.getId());
            }
        } else if (phase == THIRD_PHASE) {
            String artistName = getMessagesHistory().get(0);
            String userAnswer = getMessagesHistory().get(getMessagesHistory().size() - 1);
            if (userAnswer.toLowerCase().equals("yes")) {
                List<Artist> artists = getArtistsOfferList(artistName);
                try {
                    mongoManager.addArtist(new org.capy.musicbot.entities.Artist(service
                            .checkOutWith(artists.get(iterator))
                            .getContent()));
                } catch (ServiceException e) {
                    e.printStackTrace();
                }

                mongoManager.subscribeUser(user.getId(), artists.get(iterator).getMbid());
                messageBuilder
                        .append("I successfully added ")
                        .append(artists.get(iterator).getName())
                        .append(" to your subscribes list!");
                sendMessageToUser(user, absSender, messageBuilder.toString());
                mongoManager.finishLastCommand(user.getId());
            } else if (userAnswer.toLowerCase().equals("no")) {
                iterator++;
                phase = SECOND_PHASE;
                mongoManager.updateCommandState(user.getId(), this);
                this.execute(absSender, user);
            } else {
                messageBuilder
                        .append("Please, press \"yes\" or \"no\".");
                String[] yesOrNo = {"Yes", "No"};
                ReplyKeyboardMarkup replyKeyboardMarkup = createKeyboard(new ArrayList<String>(Arrays.asList(yesOrNo)), true, true);
                sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup);
            }
        }
    }

    private static List<Artist> getArtistsOfferList(String artistName) {
        Service service = ServiceContext.getService();
        List<Artist> artists = new ArrayList<>();

        //getting a list of artists that are associated with name written by user
        try {
            artists = service.findArtist(artistName).getContent();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return artists;
    }

    private static SendPhoto createArtistOfferMessage(User user, List<Artist> artists, int i) {
        StringBuilder messageBuilder = new StringBuilder();
        String photoUrl = artists.get(i).getImage();
        String description = artists.get(i).getShortDescription();
        String artistFullName = artists.get(i).getName();

        messageBuilder
                .append("Is that an artist you were looking for?\n\n")
                .append(artistFullName)
                .append("\n");

        //removing HTML tags from description
        if (description != null) {
            description.replaceAll("<[^>]*>(.*?)<[^>]*>", "");
            messageBuilder.append(description);
        }
        return new SendPhoto()
                .setChatId(user.getChatId())
                .setPhoto(photoUrl)
                .setCaption(messageBuilder.toString());
    }
}
