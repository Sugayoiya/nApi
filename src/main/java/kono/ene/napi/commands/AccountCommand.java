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

@Slf4j
@Component
public class AccountCommand extends BotCommand {
    private static final String COMMAND_IDENTIFIER = "account";
    private static final String COMMAND_DESCRIPTION = "login nintendo switch account, then you can use /splat3 | /other command";
    private static final String LOG_TAG = "ACCOUNT_COMMAND";

    @Resource
    private NintendoService nintendoService;

    public AccountCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long id = user.getId();
        SendMessage answer = new SendMessage();
        StringBuilder messageTextBuilder = new StringBuilder();
        nintendoService.nintendo_switch_account(id);
        answer.setChatId(chat.getId().toString());
        answer.setText(messageTextBuilder.append("login ns account success").toString());

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error(LOG_TAG, e);
            throw new RuntimeException(e);
        }

    }
}