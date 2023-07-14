package kono.ene.napi.service.chat.handler;

import cn.hutool.http.HttpResponse;
import kono.ene.napi.service.chat.properties.ChatGPTProperties;
import kono.ene.napi.service.chat.utils.ChatGPTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GPTChatAIHandler extends AbstractChatAIHandler {

    private static String AI_NAME;
    @Autowired
    private ChatGPTProperties chatGPTProperties;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected boolean isUse() {
        return chatGPTProperties.isEnable();
    }

    @Override
    public Long getChatAIUserId() {
        return chatGPTProperties.getAIUserId();
    }


    @Override
    protected String doChat(String message) {
        String text;
        {
            HttpResponse response = null;
            try {
                response = ChatGPTUtils.create(chatGPTProperties.getKey())
                        .proxyUrl(chatGPTProperties.getProxyUrl())
                        .model(chatGPTProperties.getModelName())
                        .timeout(chatGPTProperties.getTimeout())
                        .prompt(message)
                        .send();
                text = ChatGPTUtils.parseText(response);
            } catch (Exception e) {
                log.warn("gpt doChat warn:", e);
                text = "我累了，明天再聊吧";
            }
        }
        return text;
    }


    @Override
    protected boolean supports() {
        return chatGPTProperties.isEnable();
    }
}
