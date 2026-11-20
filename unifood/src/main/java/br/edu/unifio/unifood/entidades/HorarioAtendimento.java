package br.edu.unifio.unifood.entidades;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Dia, horário e local em que o vendedor atende no campus.
@Entity
@Getter
@Setter
public class HorarioAtendimento {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull (message = "O dia da semana é obrigatório (SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA ou SABADO)")
    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 10)
    private DiaSemana dia;

    @NotNull (message = "O horário de início é obrigatório")
    @JsonFormat (pattern = "HH:mm")
    @Column (nullable = false)
    private LocalTime inicio;

    @NotNull (message = "O horário de fim é obrigatório")
    @JsonFormat (pattern = "HH:mm")
    @Column (nullable = false)
    private LocalTime fim;

    @NotBlank (message = "O local é obrigatório")
    @Size (max = 100, message = "O local deve ter no máximo 100 caracteres")
    @Column (nullable = false, length = 100)
    private String local;

    // Obrigatório só no cadastro (conferido no serviço): na edição o vendedor não muda e não precisa ser enviado
    @ManyToOne (optional = false)
    private Vendedor vendedor;
}
