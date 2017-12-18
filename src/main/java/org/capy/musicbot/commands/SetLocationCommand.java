package org.capy.musicbot.commands;

import org.capy.musicbot.BotHelper;
import org.capy.musicbot.database.MongoManager;
import org.capy.musicbot.entities.User;
import org.capy.musicbot.service.Service;
import org.capy.musicbot.service.ServiceContext;
import org.capy.musicbot.service.ServiceException;
import org.capy.musicbot.service.entries.Location;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;

import static org.capy.musicbot.BotHelper.*;

/**
 * Created by enableee on 14.12.17.
 */
public class SetLocationCommand extends MultiphaseBotCommand {
    private int iteratorMaxValue;

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;
    private final static int THIRD_PHASE = 3;

    protected SetLocationCommand() {
        super();
        this.iteratorMaxValue = -1;
    }

    @Override
    public boolean execute(AbsSender absSender, User user, String[] args) throws ServiceException {
        StringBuilder messageBuilder = new StringBuilder();
        Service service = ServiceContext.getService();
        MongoManager mongoManager = MongoManager.getInstance();
        if (getCurrentPhase() == FIRST_PHASE) {
            messageBuilder.append("Please, type the name of the location: ");
            setCurrentPhase(SECOND_PHASE);
            return (queryIsExecuted(mongoManager.addCommandToCommandsList(user.getId(), this)) &&
                    BotHelper.sendMessageToUser(user, absSender, messageBuilder.toString()));
        } else if (getCurrentPhase() == SECOND_PHASE) {
            String locationName = getMessagesHistory().get(0);
            //getting the list of locations that bot can offer to user
            List<Location> locations = service.findLocation(locationName).getContent();

            //max number of artists that bot could offer to user
            iteratorMaxValue = locations.size() - 1;
            if (getIterator() <= iteratorMaxValue) {
                Location location = locations.get(getIterator());
                double latitude = location.getLatitude();
                double longtitude = location.getLongitude();
                String country = location.getCountry();
                String city = location.getCity();


                messageBuilder
                        .append("Is that a location you meant?\n\n")
                        .append("Country: ")
                        .append(country)
                        .append("\n")
                        .append("City: ")
                        .append(city);
                sendMessageToUser(user, absSender, messageBuilder.toString());
                SendLocation locationMessage = new SendLocation()
                        .setChatId(user.getChatId())
                        .setLatitude((float) latitude)
                        .setLongitude((float) longtitude);
                ReplyKeyboardMarkup replyKeyboardMarkup = createYesOrNoKeyboard();
                try {
                    absSender.execute(locationMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                setCurrentPhase(THIRD_PHASE);
                return (queryIsExecuted(mongoManager.updateCommandState(user.getId(), this)) &&
                        sendMessageWithKeyboardToUser(user, absSender, "Please, press \"yes\" or \"no\"", replyKeyboardMarkup));
            } else {
                messageBuilder
                        .append("Oops! I can't find the location you told me about. ")
                        .append("Please make sure that you typed the location name correctly ")
                        .append("and start the command again.");
                return (queryIsExecuted(mongoManager.finishLastCommand(user.getId())) &&
                        sendMessageToUser(user, absSender, messageBuilder.toString()));
            }
        } else if (getCurrentPhase() == THIRD_PHASE) {
            String userAnswer = getMessagesHistory().get(getMessagesHistory().size() - 1);
            if (userAnswer.toLowerCase().equals("yes")) {
                List<Location> locations = service.findLocation(getMessagesHistory().get(0)).getContent();
                messageBuilder
                        .append("I successfully set your location!");
                return (queryIsExecuted(mongoManager.setUserLocation(user.getId(), locations.get(getIterator()))) &&
                        queryIsExecuted(mongoManager.finishLastCommand(user.getId())) &&
                        sendMessageToUser(user, absSender, messageBuilder.toString()));
            } else if (userAnswer.toLowerCase().equals("no")) {
                setIterator(getIterator() + 1);
                setCurrentPhase(SECOND_PHASE);
                mongoManager.updateCommandState(user.getId(), this);
                return this.execute(absSender, user, null);
            } else {
                messageBuilder
                        .append("Please, press \"yes\" or \"no\".");
                ReplyKeyboardMarkup replyKeyboardMarkup = createYesOrNoKeyboard();
                return sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup);
            }
        }
        //if we reached this point then something went wrong: the phase doesn't match {1, 2, 3}
        return false;
    }
}
