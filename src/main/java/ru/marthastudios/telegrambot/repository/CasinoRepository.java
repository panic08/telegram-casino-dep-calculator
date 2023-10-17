package ru.marthastudios.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.marthastudios.telegrambot.entity.Casino;

import java.util.List;

@Repository
public interface CasinoRepository extends JpaRepository<Casino, Long> {
    List<Casino> findAllByChatId(long chatId);
    @Query("SELECT c FROM Casino c WHERE c.name = :name AND c.chatId = :chatId")
    Casino findCasinoByNameAndChatId(@Param("name") String name, @Param("chatId") long chatId);
    @Query("DELETE FROM Casino WHERE name = :name AND chatId = :chatId")
    @Modifying
    void deleteByNameAndChatId(@Param("name") String name, @Param("chatId") long chatId);
}
