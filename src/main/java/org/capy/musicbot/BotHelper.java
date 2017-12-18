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
    public static ReplyKeyboardMarkup createKeyboard(List<String> buttonsText, boolean resizeKeyboard, boolean oneTimeKeyboard) {
        StringBuilder buttonTextBuilder = new StringBuilder();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> buttons = new ArrayList<>();
        KeyboardRow buttonRow = new KeyboardRow();
        int i = 0;

        for (String text : buttonsText) {
            buttonTextBuilder.append(text);
            buttonRow.add(buttonTextBuilder.toString());
            if (i % 2 == 0) {
                buttons.add(buttonRow);
                buttonRow = new KeyboardRow();
            } else if (i == buttonsText.size())
                buttons.add(buttonRow);
            buttonTextBuilder = new StringBuilder();
        }
        replyKeyboardMarkup.setResizeKeyboard(resizeKeyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(oneTimeKeyboard);
        replyKeyboardMarkup.setKeyboard(buttons);

        return replyKeyboardMarkup;
    }

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

    public static boolean sendMessageToUser(User user, AbsSender absSender, String text) {
        SendMessage message = new SendMessage()
                .setChatId(user.getChatId())
                .setText(text);
        try {
            absSender.execute(message);
            return true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendMessageWithKeyboardToUser(User user, AbsSender absSender, String text, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage keyboardMessage = new SendMessage()
                .setChatId(user.getChatId())
                .setText(text)
                .setReplyMarkup(replyKeyboardMarkup);
        try {
            absSender.execute(keyboardMessage);
            return true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }
}
