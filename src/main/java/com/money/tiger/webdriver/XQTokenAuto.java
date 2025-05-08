package com.money.tiger.webdriver;

import com.money.tiger.biz.xq.XqProxy;
import com.money.tiger.dao.ConfigRepository;
import com.money.tiger.entity.SingleConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Component
public class XQTokenAuto {

    @Value("${chromedriver.path}")
    private String path;
    @Resource
    private XqProxy xqProxy;
    @Resource
    private ConfigRepository configRepository;

    @PostConstruct
    private void init() {
        saveToken();
        long duration = 12 * 60 * 60 * 1000L;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                saveToken();
            }
        }, duration, duration);
    }

    private void saveToken() {
        System.setProperty("webdriver.chrome.driver", path);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // 最大化窗口
//        options.addArguments("--headless"); // 无头模式
        options.addArguments("--disable-gpu"); // 禁用GPU加速
        options.addArguments("--remote-allow-origins=*"); // 允许远程
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        WebDriver driver = new ChromeDriver(options);
        driver.get("https://www.xueqiu.com");
        new WebDriverWait(driver, 1).until(ExpectedConditions.visibilityOfElementLocated(By.id("Header_logo_2mR")));
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        Set<Cookie> cookies = driver.manage().getCookies();
        StringBuilder builder = new StringBuilder();
        for (Cookie cookie : cookies) {
            builder.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }
        driver.quit();
        String token = builder.toString();
        xqProxy.flushToken(token);
        SingleConfig xqToken = configRepository.findByKey("xq_token");
        if (xqToken == null) {
            xqToken = new SingleConfig();
        }
        xqToken.setKey("xq_token");
        xqToken.setValue(token);
        configRepository.save(xqToken);
    }


    public static void main(String[] args) {
        XQTokenAuto xq = new XQTokenAuto();
        xq.path = "c:/chromedriver/chromedriver.exe";
        xq.saveToken();
    }
}
