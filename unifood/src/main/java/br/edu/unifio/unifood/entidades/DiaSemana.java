package br.edu.unifio.unifood.entidades;

import java.time.DayOfWeek;

// Dias em que há aula no campus (de segunda a sábado).
public enum DiaSemana {
    SEGUNDA,
    TERCA,
    QUARTA,
    QUINTA,
    SEXTA,
    SABADO;

    // Converte o dia da semana de uma data. Devolve null para domingo (sem atendimento).
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
