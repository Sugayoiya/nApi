package kono.ene.napi.service.telegram;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import kono.ene.napi.service.telegram.command.HelpCommand;
import kono.ene.napi.service.telegram.command.base.OrderedCommand;
import kono.ene.napi.service.telegram.handler.TelegramContext;
import kono.ene.napi.service.telegram.handler.UpdateEventEnum;
import kono.ene.napi.service.telegram.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class TelegramCustomBot extends TelegramLongPollingBot implements CommandBot, ICommandRegistry {
    public static final String LOG_TAG = "MIXER_HANDLER";
    private static final Integer CACHE_TIME = 86400;
    private final CommandRegistry commandRegistry;
    private final String botToken;
    private final String botName;
    @Resource
    private List<OrderedCommand> orderedCommands;
    @Resource
    private HelpCommand helpCommand;
    @Resource
    private TelegramService telegramService;


    /**
     * Creates a TelegramLongPollingCommandBot
     * Use ICommandRegistry's methods on this bot to register commands
     */
    public TelegramCustomBot(@Value("${telegram.token}") String botToken,
                             @Value("${telegram.name}") String botName,
                             @Autowired @Qualifier("customBotOptions") DefaultBotOptions options) {
        super(options, botToken);
        this.botToken = botToken;
        this.botName = botName;
        this.commandRegistry = new CommandRegistry(true, this::getBotUsername);
    }

    /**
     * Converts results to an answer to an inline query
     *
     * @param inlineQuery Original inline query
     * @param results     Results
     * @return AnswerInlineQuery method to answer the query
     */
    private static AnswerInlineQuery convertResultsToResponse(InlineQuery inlineQuery, List<?> results) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setCacheTime(CACHE_TIME);
        answerInlineQuery.setResults(convertResults(results));
        return answerInlineQuery;
    }

    @PostConstruct
    private void postRegister() {
        register(helpCommand);
        orderedCommands.forEach(this::register);

        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMsg = new SendMessage();
            commandUnknownMsg.setChatId(message.getChatId());
            commandUnknownMsg.setText("The command '" + message.getText() + "' is not known by this bot. Here comes some help ");
            try {
                absSender.execute(commandUnknownMsg);
            } catch (TelegramApiException e) {
                log.error(LOG_TAG, e);
            }
            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
        });
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


    @Override
    public void onUpdateReceived(Update update) {
        UpdateEventEnum event = UpdateEventEnum.fromUpdateType(update);
        telegramService.execute(TelegramContext.builder().updateEventEnum(event).update(update).build());
    }

    public boolean executeCommand(Message message) {
        return commandRegistry.executeCommand(this, message);
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