package ru.core.bot;

import akka.actor.ActorRef;
import org.jetbrains.annotations.NotNull;
import akka.actor.ActorRef;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.List;
import ru.invest.core.ConfigObject;
import java.util.Objects;

public class InvestInfoBot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private final Long chat_id;
    private final ActorRef acctorRef;
    private ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    public InvestInfoBot(@NotNull String token,
                         @NotNull String name,
                         @NotNull DefaultBotOptions defaultBotOptions,
                         @NotNull Long chat_id,
                         @NotNull ActorRef acctorRef) {
        super(defaultBotOptions);
        this.token = token;
        this.name = name;
        this.chat_id = chat_id;
        this.acctorRef = acctorRef;

        this.replyKeyboardMarkup = new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(false);

        final var keyboardFirstRow1 = new KeyboardRow();
        final var keyboardFirstRow2 = new KeyboardRow();
        final var keyboardFirstRow3 = new KeyboardRow();

        keyboardFirstRow3.add(new KeyboardButton(ConfigObject.UPDATE_TOOLS()));
        keyboardFirstRow2.add(new KeyboardButton(ConfigObject.ANALYTICS_STOP()));
        keyboardFirstRow1.add(new KeyboardButton(ConfigObject.ANALYTICS_START()));

        replyKeyboardMarkup.setKeyboard(List.of(keyboardFirstRow1, keyboardFirstRow2, keyboardFirstRow3));

        sendMessage("START_SERVER");
    }

    public InvestInfoBot(@NotNull String token,
                         @NotNull String name,
                         @NotNull Long chat_id,
                         @NotNull ActorRef acctorRef) {
        this.token = token;
        this.name = name;
        this.chat_id = chat_id;
        this.acctorRef = acctorRef;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText() && Objects.equals(update.getMessage().getChatId(), this.chat_id)) {
            try {
                acctorRef.tell(update.getMessage().getText(), acctorRef);
            } catch (Throwable ignored) {

            }
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void sendMessage(String mess) {
        var sendMessage = new SendMessage(chat_id, mess);
        sendMessage.setReplyMarkup(this.replyKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (Throwable ignored) {
        }
    }
}
