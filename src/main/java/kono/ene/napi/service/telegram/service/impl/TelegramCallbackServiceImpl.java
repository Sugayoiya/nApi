package kono.ene.napi.service.telegram.service.impl;

import jakarta.annotation.Resource;
import kono.ene.napi.service.telegram.handler.TelegramContext;
import kono.ene.napi.service.telegram.handler.processor.TelegramCallbackHandlerProcessor;
import kono.ene.napi.service.telegram.service.TelegramCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TelegramCallbackServiceImpl implements TelegramCallbackService {
    @Resource
    private TelegramCallbackHandlerProcessor callbackHandlerProcessor;

    @Override
    public void callback(TelegramContext context) {
        callbackHandlerProcessor.processContext(context);
    }
}
