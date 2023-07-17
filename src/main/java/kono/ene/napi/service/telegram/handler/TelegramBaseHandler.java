package kono.ene.napi.service.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramBaseHandler {

    boolean shouldHandle(Update update);

    void handle(Update update);
}
