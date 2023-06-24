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
public class UserMeCommand extends BotCommand {
    private static final String COMMAND_IDENTIFIER = "userme";
    private static final String COMMAND_DESCRIPTION = "update user info";
    private static final String LOG_TAG = "USER_ME_COMMAND";

    @Resource
    private NintendoService nintendoService;

    public UserMeCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
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
            throw new RuntimeException(e);
        }
    }
}