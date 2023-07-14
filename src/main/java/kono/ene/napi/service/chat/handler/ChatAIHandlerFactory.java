package kono.ene.napi.service.chat.handler;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatAIHandlerFactory {
    private static final Map<Long, AbstractChatAIHandler> CHATAI_ID_MAP = new ConcurrentHashMap<>();

    public static void register(Long aIUserId, AbstractChatAIHandler chatAIHandler) {
        CHATAI_ID_MAP.put(aIUserId, chatAIHandler);
    }

    public static AbstractChatAIHandler getChatAIHandlerById(Long userId) {
        if (userId != null) {
            return CHATAI_ID_MAP.get(userId);
        }
        return null;
    }
}
