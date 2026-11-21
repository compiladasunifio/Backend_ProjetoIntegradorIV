package br.edu.unifio.unifood.dtos;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude (JsonInclude.Include.NON_NULL)
public record ErroResposta (
        LocalDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        String caminho,
        Map<String, String> campos) {
}
