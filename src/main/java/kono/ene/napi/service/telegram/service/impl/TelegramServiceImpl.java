package kono.ene.napi.service.telegram.service.impl;

import jakarta.annotation.Resource;
import kono.ene.napi.service.telegram.handler.TelegramContext;
import kono.ene.napi.service.telegram.handler.processor.TelegramHandlerProcessor;
import kono.ene.napi.service.telegram.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TelegramServiceImpl implements TelegramService {
    @Resource
    private TelegramHandlerProcessor telegramHandlerProcessor;

    @Override
    public void execute(TelegramContext context) {
        telegramHandlerProcessor.processContext(context);
    }
}
