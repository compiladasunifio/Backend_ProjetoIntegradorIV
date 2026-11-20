package br.edu.unifio.unifood.entidades;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Nota de 1 a 5 e comentário deixados por quem comprou um produto.
@Entity
@Getter
@Setter
public class Avaliacao {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank (message = "O nome do autor é obrigatório")
    @Size (max = 60, message = "O nome do autor deve ter no máximo 60 caracteres")
    @Column (nullable = false, length = 60)
    private String autorNome;

    @NotNull (message = "A nota é obrigatória")
    @Min (value = 1, message = "A nota mínima é 1")
    @Max (value = 5, message = "A nota máxima é 5")
    @Column (nullable = false)
    private Short nota;

    @Size (max = 500, message = "O comentário deve ter no máximo 500 caracteres")
    @Column (length = 500)
    private String comentario;

    // Preenchida pelo serviço com a data do dia (o cliente não envia)
    @JsonProperty (access = JsonProperty.Access.READ_ONLY)
    @Column (nullable = false)
    private LocalDate data;

    // Obrigatório só no cadastro (conferido no serviço): na edição o produto não muda e não precisa ser enviado
    @ManyToOne (optional = false)
    private Produto produto;
}
