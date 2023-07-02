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
public class UserMeCommand extends OrderedCommand {
    private static final String COMMAND_IDENTIFIER = "userme";
    private static final String COMMAND_DESCRIPTION = "update user info";
    private static final String LOG_TAG = "USER_ME_COMMAND";

    private static final String GROUP = "nintendo";
    private static final int ORDER = 2;

    @Resource
    private NintendoService nintendoService;

    public UserMeCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION, GROUP, ORDER);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long id = user.getId();
        SendMessage answer = new SendMessage();
        StringBuilder messageTextBuilder = new StringBuilder();
        nintendoService.userInfo(id);
        answer.setChatId(chat.getId().toString());
        answer.setText(messageTextBuilder.append("update success").toString());
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error(LOG_TAG, e);
            throw new BusinessException(50001, "telegram execute error", e);
        }
    }
}