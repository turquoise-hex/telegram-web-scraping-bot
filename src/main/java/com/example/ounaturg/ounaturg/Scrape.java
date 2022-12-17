package com.example.ounaturg.ounaturg;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;


public class Scrape {

    public static String start()  {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.ounaturg.ee/mac");
        List<WebElement> ads = driver.findElements(By.className("listing-item-container"));
        String response = "";
        for (WebElement elem: ads) {
            WebElement price = elem.findElement(By.xpath(".//div[@class='listing-item-price-light']"));
            WebElement details = elem.findElement(By.xpath(".//div[@class='listing-item-details']"));
            response += elem.getAttribute("href") + "\n" + price.getText() + "\n"
                    + details.getText();

        }
        //System.out.println(response);
        return response;

    }







}
