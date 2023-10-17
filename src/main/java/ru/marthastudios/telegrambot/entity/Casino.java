package ru.marthastudios.telegrambot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "casinos_table")
public class Casino {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long chatId;
    private String name;
    private Double profit;
    private Double spent;
}
