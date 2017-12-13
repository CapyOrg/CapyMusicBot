package org.capy.musicbot;

import org.capy.musicbot.entities.Artist;
import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enableee on 13.12.17.
 */
public class BotHelper {
    public static ReplyKeyboardMarkup createKeyboardWithSubscribesList(List<Artist> subscribes) {
        StringBuilder buttonTextBuilder = new StringBuilder();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> commands = new ArrayList<>();
        KeyboardRow commandRow = new KeyboardRow();
        int i = 0;
        if (subscribes.size() != 0) {
            for (Artist artist : subscribes) {
                buttonTextBuilder
                        .append(Integer.toString(++i))
                        .append(". ")
                        .append(artist.getName())
                        .append("\n");
                commandRow.add(buttonTextBuilder.toString());
                if (i % 2 == 0) {
                    commands.add(commandRow);
                    commandRow = new KeyboardRow();
                } else if (i == subscribes.size()) {
                    commands.add(commandRow);
                }
                buttonTextBuilder = new StringBuilder();
            }
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setKeyboard(commands);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
        }
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup createYesOrNoKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> commands = new ArrayList<>();
        KeyboardRow commandRow = new KeyboardRow();
        commandRow.add("Yes");
        commandRow.add("No");
        commands.add(commandRow);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(commands);
        return replyKeyboardMarkup;
    }

    public static void sendMessageToUser(User user, AbsSender absSender, String text) {
        SendMessage message = new SendMessage()
                .setChatId(user.getChatId())
                .setText(text);
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageWithKeyboardToUser(User user, AbsSender absSender, String text, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage message = new SendMessage()
                .setChatId(user.getChatId())
                .setText(text)
                .setReplyMarkup(replyKeyboardMarkup);
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
