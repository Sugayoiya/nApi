package kono.ene.napi.service.telegram.command.nintendo;

import jakarta.annotation.Resource;
import kono.ene.napi.exception.BusinessException;
import kono.ene.napi.service.nintendo.NintendoService;
import kono.ene.napi.service.telegram.command.base.OrderedCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class BindCommand extends OrderedCommand {
    private static final String COMMAND_IDENTIFIER = "bind";
    private static final String COMMAND_DESCRIPTION = "copy the link address, and paste it behind /bind, after that you can use /account to login";
    private static final String LOG_TAG = "LOGIN_COMMAND";

    private static final String GROUP = "nintendo";
    private static final int ORDER = 1;
    @Resource
    private NintendoService nintendoService;

    public BindCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION, GROUP, ORDER);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long id = user.getId();
        SendMessage answer = new SendMessage();
        StringBuilder messageTextBuilder = new StringBuilder();
        // if no arguments, return help
        if (arguments.length == 0) {
            messageTextBuilder.append("Usage: /session <session_token>");
            answer.setChatId(chat.getId().toString());
            answer.setText(messageTextBuilder.toString());
        } else {
            String redirectUrl = arguments[0];
            nintendoService.bind(id, redirectUrl);
            answer.setChatId(chat.getId().toString());
            answer.setText(messageTextBuilder.append("bind success").toString());
        }

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error(LOG_TAG, e);
            throw new BusinessException(50001, "telegram execute error", e);
        }

    }
}