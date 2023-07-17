package kono.ene.napi.service.telegram.service;

import kono.ene.napi.service.telegram.handler.TelegramContext;

public interface TelegramCallbackService {
    void callback(TelegramContext context);
}
