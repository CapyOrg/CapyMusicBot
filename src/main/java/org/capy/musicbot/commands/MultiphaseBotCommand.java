package org.capy.musicbot.commands;


/**
 * Created by enableee on 18.12.17.
 */
public abstract class MultiphaseBotCommand extends BotCommand {
    private int currentPhase;
    private int iterator;

    public MultiphaseBotCommand() {
        this.currentPhase = 1;
        this.iterator = 0;
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(int currentPhase) {
        this.currentPhase = currentPhase;
    }

    public int getIterator() {
        return iterator;
    }

    public void setIterator(int iterator) {
        this.iterator = iterator;
    }
}
