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
import static org.capy.musicbot.database.MongoManager.isQueryExecuted;

/**
 * Created by enableee on 10.12.17.
 */
public class AddCommand extends MultiphaseBotCommand {
    private int iteratorMaxValue;

    protected AddCommand() {
        super();
        this.iteratorMaxValue = -1;
    }

    public boolean execute(AbsSender absSender, User user, String[] args) throws ServiceException {
        StringBuilder messageBuilder = new StringBuilder();
        MongoManager mongoManager = MongoManager.getInstance();
        Service service = ServiceContext.getService();
        List<Artist> artists;
        String artistName;
        switch (getCurrentPhase()) {
            case FIRST_PHASE:
                messageBuilder
                        .append("Please, type in the name of the artist you want to add to your subscribes list:");
                setCurrentPhase(SECOND_PHASE);
                isCommandExecuted &= isQueryExecuted(mongoManager.addCommandToCommandsList(user.getId(), this)) &&
                        sendMessageToUser(user, absSender, messageBuilder.toString());
                break;
            case SECOND_PHASE:
                artistName = getMessagesHistory().get(0);
                artists = getArtistsOfferList(artistName);

                //max number of artists that bot could offer to user
                iteratorMaxValue = artists.size() - 1;
                if (getIterator() <= iteratorMaxValue) {
                    SendPhoto photoMessage = createArtistOfferMessage(user, artists, getIterator());
                    ReplyKeyboardMarkup replyKeyboardMarkup = createKeyboard(new ArrayList<>(Arrays.asList("Yes", "No")), true, true);
                    try {
                        absSender.sendPhoto(photoMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    setCurrentPhase(THIRD_PHASE);
                    isCommandExecuted &= isQueryExecuted(mongoManager.updateCommandState(user.getId(), this)) &&
                            sendMessageWithKeyboardToUser(user, absSender, "Please, press \"yes\" or \"no\"", replyKeyboardMarkup);
                } else {
                    messageBuilder
                            .append("Oops! I can't find artist you told me about. ")
                            .append("Perhaps you typed artist's name wrong or this artist isn't ")
                            .append("popular enough. :<");
                    isCommandExecuted &= isQueryExecuted(mongoManager.finishLastCommand(user.getId())) &&
                            sendMessageToUser(user, absSender, messageBuilder.toString());
                }
                break;
            case THIRD_PHASE:
                artistName = getMessagesHistory().get(0);
                String userAnswer = getMessagesHistory().get(getMessagesHistory().size() - 1);
                if (userAnswer.toLowerCase().equals("yes")) {
                    artists = getArtistsOfferList(artistName);
                    mongoManager.addArtist(new org.capy.musicbot.entities.Artist(service
                            .checkOutWith(artists.get(getIterator()))
                            .getContent()));


                    messageBuilder
                            .append("I successfully added ")
                            .append(artists.get(getIterator()).getName())
                            .append(" to your subscribes list!");
                    isCommandExecuted &= isQueryExecuted(mongoManager.subscribeUser(user.getId(), artists.get(getIterator()).getMbid())) &&
                            sendMessageToUser(user, absSender, messageBuilder.toString()) &&
                            isQueryExecuted(mongoManager.finishLastCommand(user.getId()));
                } else if (userAnswer.toLowerCase().equals("no")) {
                    setIterator(getIterator() + 1);
                    setCurrentPhase(SECOND_PHASE);
                    mongoManager.updateCommandState(user.getId(), this);
                    isCommandExecuted &= this.execute(absSender, user, null);
                } else {
                    ReplyKeyboardMarkup replyKeyboardMarkup = createKeyboard(new ArrayList<>(Arrays.asList("Yes", "No")), true, true);
                    isCommandExecuted &= sendMessageWithKeyboardToUser(user, absSender, "Please, press \"yes\" or \"no\"", replyKeyboardMarkup);
                }
                break;
            default:
                return false;
        }
        return isCommandExecuted;
    }

    private static List<Artist> getArtistsOfferList(String artistName) throws ServiceException {
        Service service = ServiceContext.getService();
        //getting a list of artists that are associated with name written by user
        return service.findArtist(artistName).getContent();
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
            description = description.replaceAll("<[^>]*>(.*?)<[^>]*>", "");
            messageBuilder.append(description);
        }
        return new SendPhoto()
                .setChatId(user.getChatId())
                .setPhoto(photoUrl)
                .setCaption(messageBuilder.toString());
    }
}
