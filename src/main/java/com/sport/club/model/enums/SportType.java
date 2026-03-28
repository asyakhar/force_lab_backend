
package com.sport.club.model.enums;

public enum SportType {
    FOOTBALL("Футбол"),
    BASKETBALL("Баскетбол"),
    VOLLEYBALL("Волейбол"),
    TENNIS("Теннис"),
    ATHLETICS("Легкая атлетика"),
    SWIMMING("Плавание"),
    BOXING("Бокс"),
    WRESTLING("Борьба"),
    GYMNASTICS("Гимнастика"),
    SKIING("Лыжные гонки"),
    HOCKEY("Хоккей"),
    BIATHLON("Биатлон"),
    JUDO("Дзюдо"),
    KARATE("Карате"),
    TAEKWONDO("Тхэквондо"),
    CHESS("Шахматы"),
    OTHER("Другое");

    private final String displayName;

    SportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}