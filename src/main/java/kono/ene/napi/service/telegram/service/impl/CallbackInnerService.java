package kono.ene.napi.service.telegram.service.impl;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackInnerService {

    boolean isHandleable(String callbackQueryId);

    void handle(String callbackQueryId, Update update);
}
