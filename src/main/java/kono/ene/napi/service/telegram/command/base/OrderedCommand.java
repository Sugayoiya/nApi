package kono.ene.napi.service.telegram.command.base;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;

public abstract class OrderedCommand extends BotCommand implements IOrderedCommand {
    private final String group;
    private final int order;

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public OrderedCommand(String commandIdentifier, String description, String group, int order) {
        super(commandIdentifier, description);
        this.group = group;
        this.order = order;
    }


    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String getGroup() {
        return group;
    }


}
