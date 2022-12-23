package com.example.ounaturg.ounaturg.Service;

import com.example.ounaturg.ounaturg.BotConfig.BotConfig;
import com.example.ounaturg.ounaturg.Scrape;
import com.example.ounaturg.ounaturg.model.User;
import com.example.ounaturg.ounaturg.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;
    static final String HELP_TEXT = "This is a bot that scrapes ounaturg.ee";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> commandsList = new ArrayList<>();
        commandsList.add(new BotCommand("/iphone", "get iphones info"));
        commandsList.add(new BotCommand("/mac", "get mac info"));

        try {
            this.execute(new SetMyCommands(commandsList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error occurred while setting command list: " + e.getMessage());
        }
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

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/iphone":

                    sendMessage(chatId, "select the sorting method", secondKeyboardMarkupIPhone());
                    break;

                case "/mac":
                    sendMessage(chatId, "select the sorting method", secondKeyboardMarkupMac());
                    break;

                case "/sortAscendingMac":
                    log.info("sending the list sorted ascending");
                    registerUser(update.getMessage());
                    getMacCommandReceived(chatId, update.getMessage().getChat().getUserName(), "ascending");
                    break;

                case "/noSortingMac":
                    log.info("sending the list without sorting");
                    registerUser(update.getMessage());
                    getMacCommandReceived(chatId, update.getMessage().getChat().getUserName(), "no");
                    break;

                case "/sortAscendingIPhone":
                    log.info("sending the list sorted ascending");
                    registerUser(update.getMessage());
                    getIphonesCommandReceived(chatId, update.getMessage().getChat().getUserName(), "ascending");
                    break;

                case "/noSortingIPhone":
                    log.info("sending the list without sorting");
                    registerUser(update.getMessage());
                    getIphonesCommandReceived(chatId, update.getMessage().getChat().getUserName(), "no");
                    break;

                default:
                    sendMessage(chatId, "sorry, not supported", initialReplyKeyboardMarkup());
            }
        }
    }

    private void registerUser(Message message) {

        if(userRepository.findById(message.getChatId()).isEmpty()){

            var chatId = message.getChatId();
            var chat = message.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved " + user.toString());

        }

    }

    private void getIphonesCommandReceived(long chatId, String name, String sorting) {
        List<String> answer = Scrape.scrapeIphone();

        //sort the answer
        if(sorting.equals("ascending")) {
            sortAscending(answer);
        }

        splitAndSend(answer, 10, chatId);
        log.info("Replied to " + name);
    }

    private void getMacCommandReceived(long chatId, String name, String sorting) {
        List<String> answer = Scrape.scrapeMac();

        //sort the answer
        if(sorting.equals("ascending")) {
            sortAscending(answer);
        }

        splitAndSend(answer, 10, chatId);
        log.info("Replied to " + name);
    }

    public void sortAscending(List<String> ad){

        Comparator<String> comparator = Comparator.comparingInt(x ->
                Integer.parseInt(x.split("\\n")[1].replaceAll("[^0-9]", "")));
        ad.sort(comparator);
    }

    public void splitAndSend(List<String> listOfAds, int partitionSize, long chatId){
        List<List<String>> sublists = new LinkedList<>();
        for (int i = 0; i < listOfAds.size(); i += partitionSize) {
            sublists.add(listOfAds.subList(i,
                    Math.min(i + partitionSize, listOfAds.size())));
        }
        for(List<String> list: sublists){
            String result = "";
            for(String s: list){
                result += s;
            }
            sendMessage(chatId, result, initialReplyKeyboardMarkup());
        }
    }

    private void sendMessage(long chatId, String textToSend, ReplyKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.disableWebPagePreview();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error: " + e.getMessage());
        }
    }

    private ReplyKeyboardMarkup initialReplyKeyboardMarkup(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row;

        row = new KeyboardRow();
        row.add("/mac");
        row.add("/iphone");

        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup secondKeyboardMarkupMac(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("/noSortingMac");
        row.add("/sortAscendingMac");
        row.add("/sortDescendingMac");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup secondKeyboardMarkupIPhone(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("/noSortingIPhone");
        row.add("/sortAscendingIPhone");
        row.add("/sortDescendingIPhone");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }


}
