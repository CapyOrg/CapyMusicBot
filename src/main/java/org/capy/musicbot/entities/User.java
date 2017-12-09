package org.capy.musicbot.entities;

import com.sun.istack.internal.Nullable;
import org.capy.musicbot.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enableee on 10.12.17.
 */
public class User {
    private long id;

    private long chat_id;
    private String username;
    private String first_name;
    private String last_name;
    private boolean notificationModeOn;

    private List<Artist> subscribes = new ArrayList<>();

    private List<BotCommand> commands = new ArrayList<>(); //list of unfinished commands

    public User() {
    }

    public User(long id, long chat_id, String username, String first_name, String last_name) {
        this.id = id;
        this.chat_id = chat_id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.notificationModeOn = true;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public long getId() {
        return id;
    }

    public long getChatId() {
        return chat_id;
    }

    public boolean isNotificationModeOn() {
        return notificationModeOn;
    }

    public List<BotCommand> getCommands() {
        return commands;
    }

    public BotCommand getCurrentCommand() {
        if (!commands.isEmpty())
            return commands.get(commands.size() - 1);
        else
            return null;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", chat_id=" + chat_id +
                ", username='" + username + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", subscribes=" + subscribes +
                ", commands=" + commands +
                '}';
    }
}
