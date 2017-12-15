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

import java.util.ArrayList;
import java.util.List;

import static org.capy.musicbot.BotHelper.createYesOrNoKeyboard;
import static org.capy.musicbot.BotHelper.sendMessageToUser;
import static org.capy.musicbot.BotHelper.sendMessageWithKeyboardToUser;

/**
 * Created by enableee on 14.12.17.
 */
public class SetLocationCommand extends BotCommand {
    private int phase;
    private int iterator;
    private int iteratorMaxValue;

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;
    private final static int THIRD_PHASE = 3;

    public SetLocationCommand() {
        this.phase = FIRST_PHASE;
        this.iterator = 0;
        this.iteratorMaxValue = -1;
    }

    @Override
    public void execute(AbsSender absSender, User user) {
        StringBuilder messageBuilder = new StringBuilder();
        Service service = ServiceContext.getService();
        MongoManager mongoManager = MongoManager.getInstance();
        if (phase == FIRST_PHASE) {
            messageBuilder.append("Please, type the name of the location: ");
            BotHelper.sendMessageToUser(user, absSender, messageBuilder.toString());
            phase = SECOND_PHASE;
            mongoManager.addCommandToCommandsList(user.getId(), this);
        } else if (phase == SECOND_PHASE) {
            String locationName = getMessagesHistory().get(0);
            List<Location> locations = new ArrayList<>();
            try {
                locations = service.findLocation(locationName).getContent();
            } catch (ServiceException e) {
                e.printStackTrace();
            }

            //max number of artists that bot could offer to user
            iteratorMaxValue = locations.size() - 1;
            if (iterator <= iteratorMaxValue) {
                Location location = locations.get(iterator);
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
                sendMessageWithKeyboardToUser(user, absSender, "Please, press \"yes\" or \"no\"", replyKeyboardMarkup);
                phase = THIRD_PHASE;
                mongoManager.updateCommandState(user.getId(), this);
            } else {
                messageBuilder
                        .append("Oops! I can't find the location you told me about. ")
                        .append("Please make sure that you typed the location name correctly ")
                        .append("and start the command again.");
                sendMessageToUser(user, absSender, messageBuilder.toString());
                mongoManager.finishLastCommand(user.getId());
            }
        } else if (phase == THIRD_PHASE) {
            String userAnswer = getMessagesHistory().get(getMessagesHistory().size() - 1);
            if (userAnswer.toLowerCase().equals("yes")) {
                List<Location> locations = new ArrayList<>();
                try {
                    locations = service.findLocation(getMessagesHistory().get(0)).getContent();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                mongoManager.setUserLocation(user.getId(), locations.get(iterator));
                messageBuilder
                        .append("I successfully set your location!");
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
                ReplyKeyboardMarkup replyKeyboardMarkup = createYesOrNoKeyboard();
                sendMessageWithKeyboardToUser(user, absSender, messageBuilder.toString(), replyKeyboardMarkup);
            }
        }
    }
}
