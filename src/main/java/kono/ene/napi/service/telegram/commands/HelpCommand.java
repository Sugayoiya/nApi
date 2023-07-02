package kono.ene.napi.service.telegram.commands;

import kono.ene.napi.service.telegram.commands.base.OrderedCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.IManCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.ManCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A special bot command used for printing help messages similiar to the Linux man command.
 * The commands printed by this command should implement the {@link IManCommand} interface to provide an extended description.
 *
 * @version 1.0.0
 */
public class HelpCommand extends ManCommand {

    private static final String COMMAND_IDENTIFIER = "help";
    private static final String COMMAND_DESCRIPTION = "shows all commands. Use /help [command] for more info";
    private static final String EXTENDED_DESCRIPTION = "This command displays all commands the bot has to offer.\n /help [command] can display deeper information";

    /**
     * Create a Help command with the standard Arguments.
     */
    public HelpCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION, EXTENDED_DESCRIPTION);
    }

    /**
     * Creates a Help Command with custom identifier, description and extended Description
     *
     * @param commandIdentifier   the unique identifier of this command (e.g. the command string to enter into chat)
     * @param description         the description of this command
     * @param extendedDescription The extended Description for the Command, should provide detailed information about arguments and possible options
     */
    public HelpCommand(String commandIdentifier, String description, String extendedDescription) {
        super(commandIdentifier, description, extendedDescription);
    }

    /**
     * Returns the command and description of all supplied commands as a formatted String
     *
     * @param botCommands the Commands that should be included in the String
     * @return a formatted String containing command and description for all supplied commands
     */
    public static String getHelpText(IBotCommand... botCommands) {
        StringBuilder reply = new StringBuilder();
        Arrays.stream(botCommands)
                .filter(Objects::nonNull)
                .filter(command -> command.getClass().equals(HelpCommand.class))
                .forEach(command -> reply.append(command).append(System.lineSeparator()).append(System.lineSeparator()));
        Arrays.stream(botCommands)
                .filter(Objects::nonNull)
                .filter(command -> !command.getClass().equals(HelpCommand.class))
                .map(command -> (OrderedCommand) command)
                .collect(Collectors.groupingBy(OrderedCommand::getGroup))
                .forEach((group, commands) -> {
                    if (group != null) {
                        reply.append("[").append(group).append("]").append(System.lineSeparator());
                    }
                    commands.stream()
                            .sorted(Comparator.comparingInt(OrderedCommand::getOrder))
                            .forEach(command -> reply.append(command).append(System.lineSeparator()).append(System.lineSeparator()));
                    reply.append(System.lineSeparator());
                });
        return reply.toString();
    }

    /**
     * Returns the command and description of all supplied commands as a formatted String
     *
     * @param botCommands a collection of commands that should be included in the String
     * @return a formatted String containing command and description for all supplied commands
     */
    public static String getHelpText(Collection<IBotCommand> botCommands) {
        return getHelpText(botCommands.toArray(new IBotCommand[0]));
    }

    /**
     * Returns the command and description of all supplied commands as a formatted String
     *
     * @param registry a commandRegistry which commands are formatted into the String
     * @return a formatted String containing command and description for all supplied commands
     */
    public static String getHelpText(ICommandRegistry registry) {
        return getHelpText(registry.getRegisteredCommands());
    }

    /**
     * Reads the extended Description from a BotCommand. If the Command is not of Type {@link IManCommand}, it calls toString();
     *
     * @param command a command the extended Descriptions is read from
     * @return the extended Description or the toString() if IManCommand is not implemented
     */
    public static String getManText(IBotCommand command) {
        return command instanceof IManCommand ? getManText((IManCommand) command) : command.toString();
    }

    /**
     * Reads the extended Description from a BotCommand;
     *
     * @param command a command the extended Descriptions is read from
     * @return the extended Description
     */
    public static String getManText(IManCommand command) {
        return command.toMan();
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (absSender instanceof ICommandRegistry registry) {

            if (arguments.length > 0) {
                IBotCommand command = registry.getRegisteredCommand(arguments[0]);
                String reply = getManText(command);
                try {
                    absSender.execute(SendMessage.builder().chatId(chat.getId()).text(reply).parseMode("HTML").build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                String reply = getHelpText(registry);
                try {
                    absSender.execute(SendMessage.builder().chatId(chat.getId()).text(reply).parseMode("HTML").build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

