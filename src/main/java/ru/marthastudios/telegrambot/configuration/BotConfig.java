package ru.marthastudios.telegrambot.configuration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.properties")
public class BotConfig {

    @Value("${telegram.botName}")
    private String botUserName;

    @Value("${telegram.botToken}")
    private String token;

}