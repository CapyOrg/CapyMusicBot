package org.capy.musicbot.entities;

import org.capy.musicbot.commands.BotCommand;
import org.capy.musicbot.service.entries.Location;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by enableee on 10.12.17.
 */

@Entity(value = "users")
public class User {
    @Id
    private long id;

    private long chatId;
    private String username;
    private String firstName;
    private String lastName;
    private boolean notificationModeOn;

    @Embedded
    private Location location;

    @Reference
    private List<Artist> subscribes = new ArrayList<>();

    @Embedded
    private Map<String, Long> lastShownEvents = new HashMap<>();

    @Embedded
    private List<BotCommand> commands = new ArrayList<>(); //list of unfinished commands

    public User() {
    }

    public User(long id, long chatId, String username, String firstName, String lastName) {
        this.id = id;
        this.chatId = chatId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.notificationModeOn = true;
    }

    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
    }

    public long getChatId() {
        return chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Artist> getSubscribes() {
        return subscribes;
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

    public Location getLocation() {
        return location;
    }

    public Map<String, Long> getLastShownEvents() {
        return lastShownEvents;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", chat_id=" + chatId +
                ", username='" + username + '\'' +
                ", first_name='" + firstName + '\'' +
                ", last_name='" + lastName + '\'' +
                ", subscribes=" + subscribes +
                ", commands=" + commands +
                '}';
    }
}
