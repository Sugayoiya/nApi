package kono.ene.napi.service.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public enum UpdateEventEnum {
    MESSAGE(1, "message"),
    INLINE_QUERY(2, "inline_query"),
    CHOSEN_INLINE_QUERY(3, "chosen_inline_result"),
    CALLBACK_QUERY(4, "callback_query"),
    EDITED_MESSAGE(5, "edited_message"),
    CHANNEL_POST(6, "channel_post"),
    EDITED_CHANNEL_POST(7, "edited_channel_post"),
    SHIPPING_QUERY(8, "shipping_query"),
    PRE_CHECKOUT_QUERY(9, "pre_checkout_query"),
    POLL(10, "poll"),
    POLL_ANSWER(11, "poll_answer"),
    MY_CHAT_MEMBER(12, "my_chat_member"),
    CHAT_MEMBER(13, "chat_member"),
    CHAT_JOIN_REQUEST(14, "chat_join_request"),
    ;


    private final int value;
    private final String name;


    UpdateEventEnum(int i, String message) {
        this.value = i;
        this.name = message;
    }

    public static UpdateEventEnum fromUpdateType(Update update) {
        if (update.hasEditedMessage()) {
            return EDITED_MESSAGE;
        } else if (update.hasChannelPost()) {
            return CHANNEL_POST;
        } else if (update.hasEditedChannelPost()) {
            return EDITED_CHANNEL_POST;
        } else if (update.hasInlineQuery()) {
            return INLINE_QUERY;
        } else if (update.hasChosenInlineQuery()) {
            return CHOSEN_INLINE_QUERY;
        } else if (update.hasCallbackQuery()) {
            return CALLBACK_QUERY;
        } else if (update.hasShippingQuery()) {
            return SHIPPING_QUERY;
        } else if (update.hasPreCheckoutQuery()) {
            return PRE_CHECKOUT_QUERY;
        } else if (update.hasPoll()) {
            return POLL;
        } else if (update.hasPollAnswer()) {
            return POLL_ANSWER;
        } else if (update.hasMyChatMember()) {
            return MY_CHAT_MEMBER;
        } else if (update.hasChatMember()) {
            return CHAT_MEMBER;
        } else if (update.hasChatJoinRequest()) {
            return CHAT_JOIN_REQUEST;
        } else if (update.hasMessage()) {
            return MESSAGE;
        } else {
            throw new RuntimeException("Unknown Update type");
        }
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
