package ru.marthastudios.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marthastudios.telegrambot.entity.Casino;
import ru.marthastudios.telegrambot.repository.CasinoRepository;
import ru.marthastudios.telegrambot.service.CasinoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CasinoServiceImpl implements CasinoService {
    private final CasinoRepository casinoRepository;
    @Override
    @Transactional
    public Casino createCasino(Casino casino) {
        return casinoRepository.save(casino);
    }

    @Transactional
    @Override
    public void deleteCasinoByNameAndChatId(String name, long chatId) {
        casinoRepository.deleteByNameAndChatId(name, chatId);
    }

    @Override
    public List<Casino> getAllCasinoByChatId(long chatId) {
        return casinoRepository.findAllByChatId(chatId);
    }

    @Override
    public Casino getCasinoByNameAndChatId(String name, long chatId) {
        return casinoRepository.findCasinoByNameAndChatId(name, chatId);
    }
}
