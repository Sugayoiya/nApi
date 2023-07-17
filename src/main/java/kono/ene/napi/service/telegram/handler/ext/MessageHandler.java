package kono.ene.napi.service.telegram.handler.ext;

import jakarta.annotation.Resource;
import kono.ene.napi.service.telegram.TelegramCustomBot;
import kono.ene.napi.service.telegram.handler.TelegramBaseHandler;
import kono.ene.napi.service.telegram.handler.UpdateEventEnum;
import kono.ene.napi.service.telegram.handler.annotation.TelegramHandlerAnnotation;
import kono.ene.napi.service.telegram.service.impl.GalleryCallbackInnerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@TelegramHandlerAnnotation(events = UpdateEventEnum.MESSAGE)
@Component
public class MessageHandler implements TelegramBaseHandler {
    @Resource
    private TelegramCustomBot telegramCustomBot;

    @Resource
    private GalleryCallbackInnerServiceImpl galleryCallbackInnerService;


    @Override
    public boolean shouldHandle(Update update) {
        return false;
    }

    @Override
    public void handle(Update update) {
        log.info("[MessageHandler]: update: {}", update);
        Message message = update.getMessage();
        if (message.isCommand() && !telegramCustomBot.filter(message)) {
            if (!telegramCustomBot.executeCommand(message)) {
                //we have received a not registered command, handle it as invalid
                telegramCustomBot.processInvalidCommandUpdate(update);
            }
        } else if (message.hasText()) {
            String input = message.getText();

            // TODO callback register
            if (input.equals("start")) {

                try {
                    telegramCustomBot.execute(galleryCallbackInnerService.registerGalleryCallback(message));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
