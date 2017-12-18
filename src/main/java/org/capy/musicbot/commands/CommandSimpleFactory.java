package org.capy.musicbot.commands;

/**
 * Created by enableee on 16.12.17.
 */
public class CommandSimpleFactory {
    private static final String START_COMMAND = "/start";
    private static final String HELP_COMMAND = "/help";
    private static final String ADD_COMMAND = "/add";
    private static final String NOTIFICATIONS_OFF_COMMAND = "/notifications_off";
    private static final String NOTIFICATIONS_ON_COMMAND = "/notifications_on";
    private static final String REMOVE_COMMAND = "/remove";
    private static final String SHOW_RELEASES_COMMAND = "/show_releases";
    private static final String SHOW_RELEASES_ALL_COMMAND = "/show_releases_all";
    private static final String SHOW_SUBSCRIBES_LIST_COMMAND = "/show_subscribes_list";
    private static final String SET_LOCATION_COMMAND = "/set_location";
    private static final String SHOW_EVENTS_COMMAND = "/show_events";

    public static BotCommand createCommand(String command) {
        switch (command) {
            case START_COMMAND:
                return new StartCommand();
            case HELP_COMMAND:
                return new HelpCommand();
            case ADD_COMMAND:
                return new AddCommand();
            case NOTIFICATIONS_ON_COMMAND:
                return new NotificationsOnCommand();
            case NOTIFICATIONS_OFF_COMMAND:
                return new NotificationsOffCommand();
            case SHOW_SUBSCRIBES_LIST_COMMAND:
                return new ShowSubscribesListCommand();
            case REMOVE_COMMAND:
                return new RemoveCommand();
            case SHOW_RELEASES_COMMAND:
                return new ShowReleasesCommand();
            case SHOW_RELEASES_ALL_COMMAND:
                return new ShowReleasesAllCommand();
            case SET_LOCATION_COMMAND:
                return new SetLocationCommand();
            case SHOW_EVENTS_COMMAND:
                return new ShowEventsCommand();
            default:
                return null;
        }
    }
}
