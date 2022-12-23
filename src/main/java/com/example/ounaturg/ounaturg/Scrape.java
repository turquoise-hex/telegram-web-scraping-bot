package com.example.ounaturg.ounaturg;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Scrape {

    public static List<String> scrapeMac()  {

        ChromeDriver driver = initializeBrowser();

        driver.get("https://www.ounaturg.ee/mac");
        log.info("opened the site");

        List<WebElement> ads = driver.findElements(By.className("listing-item-container"));

        ArrayList<String> response = new ArrayList<>();
        for (WebElement elem: ads) {

            WebElement price = elem.findElement(By.xpath(".//div[@class='listing-item-price-light']"));
            WebElement details = elem.findElement(By.xpath(".//div[@class='listing-item-details']"));

            if(!details.getText().contains("iphone") && !details.getText().contains("iPhone")) {
                response.add(elem.getAttribute("href") + "\n" + price.getText() + "\n"
                        + details.getText() + "\n\n");
            }
        }
        driver.quit();
        return response;

    }


    public static List<String> scrapeIphone(){
        ChromeDriver driver = initializeBrowser();

        driver.get("https://www.ounaturg.ee/iphone");
        log.info("opened the site");

        List<WebElement> ads = driver.findElements(By.className("listing-item-container"));
        ArrayList<String> response = new ArrayList<>();
        for (WebElement elem: ads) {

            WebElement price = elem.findElement(By.xpath(".//div[@class='listing-item-price-light']"));
            WebElement details = elem.findElement(By.xpath(".//div[@class='listing-item-details']"));

            if(details.getText().contains("iPhone") && !details.getText().contains("case")) {
                response.add(elem.getAttribute("href") + "\n" + price.getText() + "\n"
                        + details.getText() + "\n\n");
            }
        }
        driver.quit();
        return response;
    }

    public static ChromeDriver initializeBrowser(){
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--disable-websecurity"); //TODO remove once the ssl cert is back
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--headless");

        return new ChromeDriver(options);
    }







}
