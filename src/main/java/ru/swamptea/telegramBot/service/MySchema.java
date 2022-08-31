package ru.swamptea.telegramBot.service;

import ru.swamptea.telegramBot.image.TextColorSchema;

public class MySchema implements TextColorSchema {
    char[] arr = {'▇', '●', '◉', '◍', '◎', '○', '☉', '◌', '-'};
    @Override
    public char convert(int color) {
        return arr[(int) (color / 28.44)];
    }
}
