package org.capy.musicbot.commands;

import org.capy.musicbot.entities.User;
import org.telegram.telegrambots.bots.AbsSender;

/**
 * Created by enableee on 14.12.17.
 */
public class ShowEventsCommand extends BotCommand {
    private int phase;
    private int iterator;

    private final static int FIRST_PHASE = 1;
    private final static int SECOND_PHASE = 2;

    public ShowEventsCommand() {
        this.phase = FIRST_PHASE;
        iterator = 0;
    }

    @Override
    public void execute(AbsSender absSender, User user) {

    }
}
