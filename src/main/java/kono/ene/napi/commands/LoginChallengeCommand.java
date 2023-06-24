package kono.ene.napi.commands;

import jakarta.annotation.Resource;
import kono.ene.napi.service.NintendoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class LoginChallengeCommand extends BotCommand {
    private static final String COMMAND_IDENTIFIER = "login";
    private static final String COMMAND_DESCRIPTION = "generate login link, click to log in, right click the \"Select this account\" button, copy the link address, and paste it behind /session ";
    private static final String LOG_TAG = "LOGIN_COMMAND";

    @Resource
    private NintendoService nintendoService;

    public LoginChallengeCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long id = user.getId();

        try {
            String s = nintendoService.loginChallenge(id);
            StringBuilder messageTextBuilder = new StringBuilder();

            SendMessage answer = new SendMessage();
            answer.setChatId(chat.getId().toString());
            answer.setText(messageTextBuilder.append(s).toString());

            absSender.execute(answer);
        } catch (NoSuchAlgorithmException | TelegramApiException e) {
            log.error(LOG_TAG, e);
            throw new RuntimeException(e);
        }
    }
}