package br.edu.unifio.unifood.dtos;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

// Corpo padrão das respostas de erro da API.
// "campos" só aparece em erros de validação (400), com a mensagem de cada campo inválido.
@JsonInclude (JsonInclude.Include.NON_NULL)
public record ErroResposta (
        LocalDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        String caminho,
        Map<String, String> campos) {
}
