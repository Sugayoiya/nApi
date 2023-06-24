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
public class Splat3Command extends BotCommand {
    private static final String COMMAND_IDENTIFIER = "splat3";
    private static final String COMMAND_DESCRIPTION = "splat3 webservice token";
    private static final String LOG_TAG = "SPLAT3_COMMAND";

    @Resource
    private NintendoService nintendoService;

    public Splat3Command() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long id = user.getId();
        SendMessage answer = new SendMessage();
        StringBuilder messageTextBuilder = new StringBuilder();
        nintendoService.web_service_token(id, "Splatoon 3（斯普拉遁 3）");
        answer.setChatId(chat.getId().toString());
        answer.setText(messageTextBuilder.append("splatoon3 webservice token refresh success").toString());

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error(LOG_TAG, e);
            throw new RuntimeException(e);
        }

    }
}