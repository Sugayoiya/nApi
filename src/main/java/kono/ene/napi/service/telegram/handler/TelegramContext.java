package kono.ene.napi.service.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelegramContext {
    private UpdateEventEnum updateEventEnum;
    private Update update;
    private CallbackQuery callbackQuery;
}
