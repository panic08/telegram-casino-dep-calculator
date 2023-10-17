package ru.marthastudios.telegrambot.service;

import ru.marthastudios.telegrambot.entity.Casino;

import java.util.List;

public interface CasinoService {
    Casino createCasino(Casino casino);
    void deleteCasinoByNameAndChatId(String name, long chatId);
    List<Casino> getAllCasinoByChatId(long chatId);
    Casino getCasinoByNameAndChatId(String name, long chatId);
}
