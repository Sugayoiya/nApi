package kono.ene.napi.service.telegram.service;

import kono.ene.napi.service.telegram.handler.TelegramContext;

public interface TelegramService {
    void execute(TelegramContext context);
}
