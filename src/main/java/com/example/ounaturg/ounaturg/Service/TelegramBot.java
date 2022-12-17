package com.example.ounaturg.ounaturg.Service;


import com.example.ounaturg.ounaturg.BotConfig.BotConfig;
import com.example.ounaturg.ounaturg.Scrape;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch(messageText) {
                case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                default:
                    sendMessage(chatId, "sorry, not supported");
                    }
            }
        }

    private void startCommandReceived(long chatId, String name) {
        //String answer = "Hi, " + name + ", nice to meet you!";

        String answer = Scrape.start();

        System.out.println(answer);

        sendMessage(chatId, answer.substring(0, 2000));
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try{
            execute(message);
        } catch (TelegramApiException e) {

        }
    }
}
