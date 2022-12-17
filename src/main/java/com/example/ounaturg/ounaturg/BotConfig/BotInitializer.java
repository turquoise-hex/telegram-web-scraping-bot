package com.example.ounaturg.ounaturg.BotConfig;


import com.example.ounaturg.ounaturg.Service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    @Autowired
    TelegramBot bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBitsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBitsApi.registerBot(bot);
        } catch (TelegramApiException e){

        }


    }
}
