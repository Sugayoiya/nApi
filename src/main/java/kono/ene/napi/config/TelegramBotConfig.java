package kono.ene.napi.config;

import jakarta.annotation.Resource;
import kono.ene.napi.service.telegram.MixHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@ConditionalOnProperty(prefix = "nintendo.telegram", name = "enable", havingValue = "true")
public class TelegramBotConfig {
    @Resource
    private MixHandler mixHandler;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(mixHandler);
        return telegramBotsApi;
    }
}
