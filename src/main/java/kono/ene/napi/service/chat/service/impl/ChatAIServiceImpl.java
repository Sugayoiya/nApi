package kono.ene.napi.service.chat.service.impl;

import kono.ene.napi.service.chat.handler.AbstractChatAIHandler;
import kono.ene.napi.service.chat.handler.ChatAIHandlerFactory;
import kono.ene.napi.service.chat.service.IChatAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class ChatAIServiceImpl implements IChatAIService {
    @Override
    public String chat(String message) {
        AbstractChatAIHandler chatAI = ChatAIHandlerFactory.getChatAIHandlerById(1L);
        if (chatAI != null) {
            try {
                return chatAI.chat(message).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}