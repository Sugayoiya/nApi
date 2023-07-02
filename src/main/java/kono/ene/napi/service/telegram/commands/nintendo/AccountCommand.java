package kono.ene.napi.service.telegram.commands.nintendo;

import jakarta.annotation.Resource;
import kono.ene.napi.exception.BusinessException;
import kono.ene.napi.service.nintendo.NintendoService;
import kono.ene.napi.service.telegram.commands.base.OrderedCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class AccountCommand extends OrderedCommand {
    private static final String COMMAND_IDENTIFIER = "account";
    private static final String COMMAND_DESCRIPTION = "login nintendo switch account, then you can use /splat3 | /other command";
    private static final String LOG_TAG = "ACCOUNT_COMMAND";

    private static final String GROUP = "nintendo";
    private static final int ORDER = 3;

    @Resource
    private NintendoService nintendoService;

    public AccountCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION, GROUP, ORDER);
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
            throw new BusinessException(50001, "telegram execute error", e);
        }
    }
}