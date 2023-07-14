package kono.ene.napi.service.chat.handler;

import cn.hutool.http.HttpResponse;
import kono.ene.napi.service.chat.properties.ChatGLM2Properties;
import kono.ene.napi.service.chat.utils.ChatGLM2Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Slf4j
@Component
public class ChatGLM2Handler extends AbstractChatAIHandler {

    private static final List<String> ERROR_MSG = Arrays.asList(
            "还摸鱼呢？你不下班我还要下班呢。。。。",
            "没给钱，矿工了。。。。",
            "服务器被你们玩儿坏了。。。。",
            "你们这群人，我都不想理你们了。。。。",
            "艾特我那是另外的价钱。。。。",
            "得加钱");


    private static final Random RANDOM = new Random();

    private static String AI_NAME;

    @Autowired
    private ChatGLM2Properties glm2Properties;

    private static String getErrorText() {
        int index = RANDOM.nextInt(ERROR_MSG.size());
        return ERROR_MSG.get(index);
    }

    @Override
    protected void init() {
        super.init();
        if (isUse()) {
            // TODO
        }
    }

    @Override
    protected boolean isUse() {
        return glm2Properties.isEnable();
    }

    @Override
    public Long getChatAIUserId() {
        return glm2Properties.getAIUserId();
    }

    @Override
    protected String doChat(String message) {

        String text;
        {
            HttpResponse response = null;
            try {
                response = ChatGLM2Utils
                        .create()
                        .url(glm2Properties.getUrl())
                        .prompt(message)
                        .timeout(glm2Properties.getTimeout())
                        .send();
                text = ChatGLM2Utils.parseText(response);
            } catch (Exception e) {
                log.warn("glm2 doChat warn:", e);
                return getErrorText();
            }
        }
        return text;
    }

    @Override
    protected boolean supports() {
        return glm2Properties.isEnable();
    }
}
