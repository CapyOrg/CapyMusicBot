package org.capy.musicbot.commands;


/**
 * Created by enableee on 18.12.17.
 */
public abstract class MultiphaseBotCommand extends BotCommand {
    private int currentPhase;
    private int iterator;

    protected final static int FIRST_PHASE = 1;
    protected final static int SECOND_PHASE = 2;
    protected final static int THIRD_PHASE = 3;

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
