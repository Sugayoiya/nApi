package kono.ene.napi.service.chat.handler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Slf4j
public abstract class AbstractChatAIHandler {

    @PostConstruct
    protected void init() {
        if (isUse()) {
            ChatAIHandlerFactory.register(getChatAIUserId(), this);
        }
    }

    /**
     * 是否启用
     *
     * @return boolean
     */
    protected abstract boolean isUse();

    // 获取机器人id
    public abstract Long getChatAIUserId();

    public CompletableFuture<String> chat(String message) {
        if (!supports()) {
            return null;
        }
        try (var ex = Executors.newVirtualThreadPerTaskExecutor()) {
            return CompletableFuture.supplyAsync(() -> {
                String text = doChat(message);
                if (StringUtils.isNotBlank(text)) {
                    return text;
                }
                return null;
            }, ex);
        }
    }

    /**
     * 支持
     *
     * @return boolean true 支持 false 不支持
     */
    protected abstract boolean supports();

    /**
     * 执行聊天
     *
     * @param message 消息
     * @return {@link String} AI回答的内容
     */
    protected abstract String doChat(String message);


//    protected void answerMsg(String text, Message replyMessage) {
//        UserInfoResp userInfo = userService.getUserInfo(replyMessage.getFromUid());
//        text = "@" + userInfo.getName() + " " + text;
//        if (text.length() < 800) {
//            save(text, replyMessage);
//        } else {
//            int maxLen = 800;
//            int len = text.length();
//            int count = (len + maxLen - 1) / maxLen;
//
//            for (int i = 0; i < count; i++) {
//                int start = i * maxLen;
//                int end = Math.min(start + maxLen, len);
//                save(text.substring(start, end), replyMessage);
//            }
//        }
//    }

//    private void save(String text, Message replyMessage) {
//        Long roomId = replyMessage.getRoomId();
//        Long uid = replyMessage.getFromUid();
//        Long id = replyMessage.getId();
//        ChatMessageReq answerReq = new ChatMessageReq();
//        answerReq.setRoomId(roomId);
//        answerReq.setMsgType(MessageTypeEnum.TEXT.getType());
//        TextMsgReq textMsgReq = new TextMsgReq();
//        textMsgReq.setContent(text);
//        textMsgReq.setReplyMsgId(replyMessage.getId());
//        textMsgReq.setAtUidList(Collections.singletonList(uid));
//        answerReq.setBody(textMsgReq);
//        chatService.sendMsg(answerReq, getChatAIUserId());
//    }

}
