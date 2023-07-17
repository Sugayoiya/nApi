package kono.ene.napi.service.telegram.handler.ext;

import jakarta.annotation.Resource;
import kono.ene.napi.service.telegram.handler.TelegramBaseHandler;
import kono.ene.napi.service.telegram.handler.TelegramContext;
import kono.ene.napi.service.telegram.handler.UpdateEventEnum;
import kono.ene.napi.service.telegram.handler.annotation.TelegramHandlerAnnotation;
import kono.ene.napi.service.telegram.service.TelegramCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@TelegramHandlerAnnotation(events = UpdateEventEnum.CALLBACK_QUERY)
@Component
public class CallbackHandler implements TelegramBaseHandler {
    @Resource
    private TelegramCallbackService telegramCallbackService;


    @Override
    public boolean shouldHandle(Update update) {
        return false;
    }

    @Override
    public void handle(Update update) {
        log.info("[CallbackHandler]: update: {}", update);
        TelegramContext telegramContext = TelegramContext.builder()
                .update(update)
                .callbackQuery(update.getCallbackQuery())
                .updateEventEnum(UpdateEventEnum.CALLBACK_QUERY).build();
        telegramCallbackService.callback(telegramContext);
    }
}
