package kono.ene.napi.service.telegram;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import kono.ene.napi.commands.HelpCommand;
import kono.ene.napi.commands.nintendo.AccountCommand;
import kono.ene.napi.commands.nintendo.BindCommand;
import kono.ene.napi.commands.nintendo.LoginChallengeCommand;
import kono.ene.napi.commands.nintendo.UserMeCommand;
import kono.ene.napi.commands.splatoon.Splat3Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;


/**
 * Inline handler and command handler
 */
@Slf4j
@Component
public class MixHandler extends TelegramLongPollingBot implements CommandBot, ICommandRegistry {
    public static final String LOG_TAG = "MIXER_HANDLER";
    private static final Integer CACHE_TIME = 86400;
    private final CommandRegistry commandRegistry;
    private final String botToken;
    private final String botName;
    @Resource
    private LoginChallengeCommand loginChallengeCommand;
    @Resource
    private BindCommand bindCommand;
    @Resource
    private UserMeCommand userMeCommand;
    @Resource
    private AccountCommand accountCommand;
    @Resource
    private Splat3Command splat3Command;


    /**
     * Creates a TelegramLongPollingCommandBot
     * Use ICommandRegistry's methods on this bot to register commands
     */
    public MixHandler(@Value("${telegram.token}") String botToken,
                      @Value("${telegram.name}") String botName) {
        super(new DefaultBotOptions(), botToken);
        this.botToken = botToken;
        this.botName = botName;
        this.commandRegistry = new CommandRegistry(true, this::getBotUsername);
    }

    /**
     * Converts resutls from RaeService to an answer to an inline query
     *
     * @param inlineQuery Original inline query
     * @param results     Results from RAE service
     * @return AnswerInlineQuery method to answer the query
     */
    private static AnswerInlineQuery converteResultsToResponse(InlineQuery inlineQuery, List<?> results) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setCacheTime(CACHE_TIME);
        answerInlineQuery.setResults(convertResults(results));
        return answerInlineQuery;
    }

    /**
     * Converts results from RaeService to a list of InlineQueryResultArticles
     *
     * @param raeResults Results from rae service
     * @return List of InlineQueryResult
     */
    private static List<InlineQueryResult> convertResults(List<?> raeResults) {
        List<InlineQueryResult> results = new ArrayList<>();

        InputTextMessageContent messageContent = new InputTextMessageContent();
        messageContent.setDisableWebPagePreview(true);
        messageContent.setMessageText("test message");
        InlineQueryResultArticle article = new InlineQueryResultArticle();
        article.setInputMessageContent(messageContent);
        article.setId("1");
        article.setTitle("test title");
        article.setDescription("test description");
        results.add(article);

        return results;
    }

    @PostConstruct
    private void postRegister() {
        HelpCommand helpCommand = new HelpCommand();
        register(helpCommand);

        register(loginChallengeCommand);
        register(bindCommand);
        register(userMeCommand);
        register(accountCommand);
        register(splat3Command);

        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot. Here comes some help ");
            try {
                absSender.execute(commandUnknownMessage);
            } catch (TelegramApiException e) {
                log.error(LOG_TAG, e);
            }
            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
        });
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasInlineQuery()) {
            handleIncomingInlineQuery(update.getInlineQuery());
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isCommand() && !filter(message)) {
                if (!commandRegistry.executeCommand(this, message)) {
                    //we have received a not registered command, handle it as invalid
                    processInvalidCommandUpdate(update);
                }
                return;
            }
        }
        processNonCommandUpdate(update);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText()) {
                SendMessage echoMessage = new SendMessage();
                echoMessage.setChatId(message.getChatId());
                echoMessage.setText("Hey here's your message:\n" + message.getText());

                try {
                    execute(echoMessage);
                } catch (TelegramApiException e) {
                    log.error(LOG_TAG, e);
                }
            }
        }
    }

    private void handleIncomingInlineQuery(InlineQuery inlineQuery) {
        String query = inlineQuery.getQuery();
        log.debug("{} Searching: {}", LOG_TAG, query);
        try {
            if (!query.isEmpty()) {
                execute(converteResultsToResponse(inlineQuery, new ArrayList<>()));
            } else {
                execute(converteResultsToResponse(inlineQuery, new ArrayList<>()));
            }
        } catch (TelegramApiException e) {
            log.error(LOG_TAG, e);
        }
    }

    @Override
    public final boolean register(IBotCommand botCommand) {
        return commandRegistry.register(botCommand);
    }

    @Override
    public final Map<IBotCommand, Boolean> registerAll(IBotCommand... botCommands) {
        return commandRegistry.registerAll(botCommands);
    }

    @Override
    public final boolean deregister(IBotCommand botCommand) {
        return commandRegistry.deregister(botCommand);
    }

    @Override
    public final Map<IBotCommand, Boolean> deregisterAll(IBotCommand... botCommands) {
        return commandRegistry.deregisterAll(botCommands);
    }

    @Override
    public final Collection<IBotCommand> getRegisteredCommands() {
        return commandRegistry.getRegisteredCommands();
    }

    @Override
    public void registerDefaultAction(BiConsumer<AbsSender, Message> defaultConsumer) {
        commandRegistry.registerDefaultAction(defaultConsumer);
    }

    @Override
    public final IBotCommand getRegisteredCommand(String commandIdentifier) {
        return commandRegistry.getRegisteredCommand(commandIdentifier);
    }

    /**
     * @return Bot username
     */
    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}