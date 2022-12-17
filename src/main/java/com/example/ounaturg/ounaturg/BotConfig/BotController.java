package com.example.ounaturg.ounaturg.BotConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("")
public class BotController {

    private final String token;

    @Autowired
    public BotController(@Value("${telegram.bot.token}") String token) {
        this.token = token;
    }


    @GetMapping("/sendMessage")
    public String get(){
        System.out.println(token);
        return token;
    }



}
