package kono.ene.napi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
public class TelegramBotOptionConfig {

    @Bean
    public DefaultBotOptions customBotOptions() {
        DefaultBotOptions defaultBotOptions = new DefaultBotOptions();
        defaultBotOptions.setMaxThreads(10);
        return defaultBotOptions;
    }
}
