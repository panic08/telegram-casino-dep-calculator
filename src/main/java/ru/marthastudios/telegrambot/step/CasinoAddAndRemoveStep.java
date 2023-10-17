package ru.marthastudios.telegrambot.step;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CasinoAddAndRemoveStep {
    private int step;
    private String casinoName;
}
