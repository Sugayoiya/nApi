package kono.ene.napi.service.telegram;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import kono.ene.napi.service.telegram.commands.HelpCommand;
import kono.ene.napi.service.telegram.commands.nintendo.AccountCommand;
import kono.ene.napi.service.telegram.commands.nintendo.BindCommand;
import kono.ene.napi.service.telegram.commands.nintendo.LoginChallengeCommand;
import kono.ene.napi.service.telegram.commands.nintendo.UserMeCommand;
import kono.ene.napi.service.telegram.commands.splatoon.Splat3Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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

    final private String BACK = "⬅️  Back";
    final private String NEXT = "Next ➡️";
    final private String INDEX_OUT_OF_RANGE = "Requested index is out of range!";
    private final ArrayList<String[]> urls;


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

        this.urls = new ArrayList<>();
        this.addUrls();
    }

    /**
     * Converts resutls from RaeService to an answer to an inline query
     *
     * @param inlineQuery Original inline query
     * @param results     Results from RAE service
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

    private void addUrls() {

        /*
         * Just some sample links of my fav images from elektrollart.de
         */
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=2964", "http://www.elektrollart.de/wp-content/uploads/deer-724x1024.png", "Deer Nature (cc-by)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=2960", "http://www.elektrollart.de/wp-content/uploads/butterfly_wallpaper_by_elektroll-d424m9d-1024x576.png", "Butterfly Wallpaper (cc-by)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=2897", "http://www.elektrollart.de/wp-content/uploads/ilovefs_wallpaper-1024x576.png", "I Love Free Software – Wallpaper (CC0)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=3953", "http://www.elektrollart.de/wp-content/uploads/diaspora_wallpaper_by_elektroll-d4anyj4-1024x576.png", "diaspora Wallpaper (CC-BY-SA)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=549", "http://www.elektrollart.de/wp-content/uploads/diaspora_flower-1024x576.png", "Diaspora Digital Wallpaper (CC-BY-SA)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=534", "http://www.elektrollart.de/wp-content/uploads/debian-butterfly-1024x576.png", "Debian-Butterfly Wallpaper (CC-BY-SA)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=531", "http://www.elektrollart.de/wp-content/uploads/cc-white-1920x1080-1024x576.png", "CC-Wallpaper (CC-BY-NC-SA)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=526", "http://www.elektrollart.de/wp-content/uploads/debian-gal-1920x1080-1024x576.png", "Debian Wallpaper (CC-BY-SA)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=523", "http://www.elektrollart.de/wp-content/uploads/Ubuntusplash-1920x1080-1024x576.png", "Ubuntu Wallpaper (CC-BY-NC-SA)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=559", "http://www.elektrollart.de/wp-content/uploads/skullgirll_a-1024x576.png", "Skullgirl Wallpapers (CC-BY-NC-SA)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=559", "http://www.elektrollart.de/wp-content/uploads/skullgirll_b-1024x576.png", "Skullgirl Wallpapers (CC-BY-NC-SA)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=847", "http://www.elektrollart.de/wp-content/uploads/archlinux_wallpaper-1024x576.png", "ArchLinux (CC0)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=1381", "http://www.elektrollart.de/wp-content/uploads/tuxxi-small-724x1024.png", "Piep (CC-BY)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=4264", "http://www.elektrollart.de/wp-content/uploads/Thngs_left_unsaid-724x1024.jpg", "Things Left Unsaid (CC-BY)"});
        this.urls.add(new String[]{"http://www.elektrollart.de/?p=2334", "http://www.elektrollart.de/wp-content/uploads/redpanda-1024x826.png", "<3 mozilla (CC0)"});
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
            } else if (message.hasText()) {
                String input = message.getText();

                if (input.equals("start")) {
                    SendMessage sendMessagerequest = new SendMessage();
                    sendMessagerequest.setChatId(message.getChatId().toString());
                    /*
                     * we just add the first link from our array
                     *
                     * We use markdown to embedd the image
                     */
                    sendMessagerequest.setText("[" + this.urls.get(0)[2] + "](" + this.urls.get(0)[1] + ")");
                    sendMessagerequest.enableMarkdown(true);

                    sendMessagerequest.setReplyMarkup(this.getGalleryView(0, -1));


                    try {
                        execute(sendMessagerequest);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackquery = update.getCallbackQuery();
            String[] data = callbackquery.getData().split(":");
            int index = Integer.parseInt(data[2]);

            if (data[0].equals("gallery")) {

                InlineKeyboardMarkup markup = null;

                if (data[1].equals("back")) {
                    markup = this.getGalleryView(Integer.parseInt(data[2]), 1);
                    if (index > 0) {
                        index--;
                    }
                } else if (data[1].equals("next")) {
                    markup = this.getGalleryView(Integer.parseInt(data[2]), 2);
                    if (index < this.urls.size() - 1) {
                        index++;
                    }
                } else if (data[1].equals("text")) {
                    try {
                        this.sendAnswerCallbackQuery("Please use one of the given actions below, instead.", false, callbackquery);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                if (markup == null) {
                    try {
                        this.sendAnswerCallbackQuery(INDEX_OUT_OF_RANGE, false, callbackquery);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {

                    EditMessageText editMarkup = new EditMessageText();
                    editMarkup.setChatId(callbackquery.getMessage().getChatId().toString());
                    editMarkup.setInlineMessageId(callbackquery.getInlineMessageId());
                    editMarkup.setText("[" + this.urls.get(index)[2] + "](" + this.urls.get(index)[1] + ")");
                    editMarkup.enableMarkdown(true);
                    editMarkup.setMessageId(callbackquery.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(markup);
                    try {
                        execute(editMarkup);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                }


            }
        }
    }

    /**
     * @param text          The text that should be shown
     * @param alert         If the text should be shown as a alert or not
     * @param callbackquery
     * @throws TelegramApiException
     */
    private void sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) throws TelegramApiException {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        execute(answerCallbackQuery);
    }

    /**
     * @param index  Index of the current image
     * @param action What button was clicked
     * @return
     */
    private InlineKeyboardMarkup getGalleryView(int index, int action) {
        /*
         * action = 1 -> back
         * action = 2 -> next
         * action = -1 -> nothing
         */

        if (action == 1 && index > 0) {
            index--;
        } else if ((action == 1 && index == 0)) {
            return null;
        } else if (action == 2 && index >= this.urls.size() - 1) {
            return null;
        } else if (action == 2) {
            index++;
        }

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(InlineKeyboardButton.builder().text(this.urls.get(index)[2]).callbackData("gallery:text:" + index).build());

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(InlineKeyboardButton.builder().text(BACK).callbackData("gallery:back:" + index).build());
        rowInline2.add(InlineKeyboardButton.builder().text(NEXT).callbackData("gallery:next:" + index).build());

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        rowInline3.add(InlineKeyboardButton.builder().text("Link").url(this.urls.get(index)[0]).build());


        rowsInline.add(rowInline);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    // TODO move to service
    private void handleIncomingInlineQuery(InlineQuery inlineQuery) {
        String query = inlineQuery.getQuery();
        log.debug("{} Searching: {}", LOG_TAG, query);
        try {
            if (!query.isEmpty()) {
                execute(convertResultsToResponse(inlineQuery, new ArrayList<>()));
            } else {
                execute(convertResultsToResponse(inlineQuery, new ArrayList<>()));
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