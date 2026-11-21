package br.edu.unifio.unifood.entidades;

import java.time.DayOfWeek;

public enum DiaSemana {
    SEGUNDA,
    TERCA,
    QUARTA,
    QUINTA,
    SEXTA,
    SABADO;

    public static DiaSemana de (DayOfWeek diaDaSemana) {
        return switch (diaDaSemana) {
            case MONDAY -> SEGUNDA;
            case TUESDAY -> TERCA;
            case WEDNESDAY -> QUARTA;
            case THURSDAY -> QUINTA;
            case FRIDAY -> SEXTA;
            case SATURDAY -> SABADO;
            case SUNDAY -> null;
        };
    }
}
