package org.example.bot.util;

import org.springframework.stereotype.Component;

/**
 * Утилитарный класс
 */
@Component
public class NumberUtils {
    /**
     * Узнать, находится ли в строке только лишь число
     * @return true - если только цифры в строке, false - все остальные случаи.
     */
    public boolean isNumber(String number) {
        return number.matches("^-?\\d+$");
    }
}
