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
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * This handler mainly works with commands to demonstrate the Commands feature of the API
 *
 * @author Timo Schulz (Mit0x2)
 */
@Slf4j
@Component
public class CommandsHandler extends TelegramLongPollingCommandBot {
    public static final String LOG_TAG = "COMMANDS_HANDLER";
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

    public CommandsHandler(@Value("${telegram.name}") String name,
                           @Value("${telegram.token}") String token) {
        super(name);
        this.botToken = token;
        this.botName = name;
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
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}