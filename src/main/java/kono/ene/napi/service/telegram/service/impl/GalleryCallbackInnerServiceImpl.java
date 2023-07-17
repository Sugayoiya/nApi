package kono.ene.napi.service.telegram.service.impl;

import jakarta.annotation.Resource;
import kono.ene.napi.service.telegram.TelegramCustomBot;
import kono.ene.napi.service.telegram.handler.annotation.TelegramCallbackAnnotation;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kono.ene.napi.constant.CallbackConstant.GALLERY;

@Service
@TelegramCallbackAnnotation(callback = GALLERY)
public class GalleryCallbackInnerServiceImpl implements CallbackInnerService {
    private static final String BACK = "⬅️  Back";
    private static final String NEXT = "Next ➡️";
    private static final String INDEX_OUT_OF_RANGE = "Requested index is out of range!";
    private final ArrayList<String[]> urls = new ArrayList<>(
            Arrays.asList(new String[]{"http://www.elektrollart.de/?p=2964", "http://www.elektrollart.de/wp-content/uploads/deer-724x1024.png", "Deer Nature (cc-by)"},
                    new String[]{"http://www.elektrollart.de/?p=2960", "http://www.elektrollart.de/wp-content/uploads/butterfly_wallpaper_by_elektroll-d424m9d-1024x576.png", "Butterfly Wallpaper (cc-by)"},
                    new String[]{"http://www.elektrollart.de/?p=2897", "http://www.elektrollart.de/wp-content/uploads/ilovefs_wallpaper-1024x576.png", "I Love Free Software – Wallpaper (CC0)"},
                    new String[]{"http://www.elektrollart.de/?p=3953", "http://www.elektrollart.de/wp-content/uploads/diaspora_wallpaper_by_elektroll-d4anyj4-1024x576.png", "diaspora Wallpaper (CC-BY-SA)"},
                    new String[]{"http://www.elektrollart.de/?p=549", "http://www.elektrollart.de/wp-content/uploads/diaspora_flower-1024x576.png", "Diaspora Digital Wallpaper (CC-BY-SA)"})
    );
    @Resource
    private TelegramCustomBot telegramCustomBot;

    @Override
    public boolean isHandleable(String callbackQueryId) {
        return GALLERY.equals(callbackQueryId.split(":")[0]);
    }

    @Override
    public void handle(String callbackQueryId, Update update) {
        if (!isHandleable(callbackQueryId)) {
            return;
        }

        CallbackQuery callbackquery = update.getCallbackQuery();
        String[] data = callbackquery.getData().split(":");
        int index = Integer.parseInt(data[2]);

        if (data[0].equals(GALLERY)) {

            InlineKeyboardMarkup markup = null;

            switch (data[1]) {
                case "back" -> {
                    markup = this.getGalleryView(Integer.parseInt(data[2]), 1);
                    if (index > 0) {
                        index--;
                    }
                }
                case "next" -> {
                    markup = this.getGalleryView(Integer.parseInt(data[2]), 2);
                    if (index < this.urls.size() - 1) {
                        index++;
                    }
                }
                case "text" -> {
                    try {
                        sendAnswerCallbackQuery("Please use one of the given actions below, instead.", false, callbackquery);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (markup == null) {
                try {
                    sendAnswerCallbackQuery(INDEX_OUT_OF_RANGE, false, callbackquery);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {

                EditMessageText editMarkup = new EditMessageText();
                editMarkup.setChatId(callbackquery.getMessage().getChatId().toString());
                editMarkup.setInlineMessageId(callbackquery.getInlineMessageId());
                editMarkup.setText("[" + this.urls.get(index)[2] + "](" + this.urls.get(index)[1] + ")");
                editMarkup.enableMarkdown(true);
                editMarkup.setMessageId(callbackquery.getMessage().getMessageId());
                editMarkup.setReplyMarkup(markup);
                try {
                    telegramCustomBot.execute(editMarkup);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    public SendMessage registerGalleryCallback(Message message) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(message.getChatId().toString());
        /*
         * we just add the first link from our array
         *
         * We use markdown to embedd the image
         */
        sendMessageRequest.setText("[" + this.urls.get(0)[2] + "](" + this.urls.get(0)[1] + ")");
        sendMessageRequest.enableMarkdown(true);

        sendMessageRequest.setReplyMarkup(this.getGalleryView(0, -1));
        return sendMessageRequest;
    }


    /**
     * @param text          The text that should be shown
     * @param alert         If the text should be shown as an alert or not
     * @param callBackQuery The callBackQuery that should be answered
     * @throws TelegramApiException If something goes wrong
     */
    private void sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callBackQuery) throws TelegramApiException {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callBackQuery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        telegramCustomBot.execute(answerCallbackQuery);
    }

    /**
     * @param index  Index of the current image
     * @param action What button was clicked
     * @return The new markup
     */
    private InlineKeyboardMarkup getGalleryView(int index, int action) {
        /*
         * action = 1 -> back
         * action = 2 -> next
         * action = -1 -> nothing
         */

        if (action == 1 && index > 0) {
            index--;
        } else if ((action == 1 && index == 0)) {
            return null;
        } else if (action == 2 && index >= this.urls.size() - 1) {
            return null;
        } else if (action == 2) {
            index++;
        }

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(InlineKeyboardButton.builder().text(this.urls.get(index)[2]).callbackData(GALLERY + ":text:" + index).build());

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(InlineKeyboardButton.builder().text(BACK).callbackData(GALLERY + ":back:" + index).build());
        rowInline2.add(InlineKeyboardButton.builder().text(NEXT).callbackData(GALLERY + ":next:" + index).build());

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        rowInline3.add(InlineKeyboardButton.builder().text("Link").url(this.urls.get(index)[0]).build());


        rowsInline.add(rowInline);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
