package br.edu.unifio.unifood.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Pedido de um comprador para retirar um produto com o vendedor,
// em um dia e horário em que ele está atendendo no campus.
@Entity
@Getter
@Setter
public class Encomenda {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank (message = "O nome do cliente é obrigatório")
    @Size (max = 100, message = "O nome do cliente deve ter no máximo 100 caracteres")
    @Column (nullable = false, length = 100)
    private String clienteNome;

    @NotBlank (message = "O telefone do cliente é obrigatório")
    @Pattern (regexp = "\\d{10,11}", message = "O telefone deve ter 10 ou 11 dígitos (DDD + número), somente números")
    @Column (nullable = false, length = 11)
    private String clienteTelefone;

    @NotNull (message = "A quantidade é obrigatória")
    @Min (value = 1, message = "A quantidade mínima é 1")
    @Max (value = 20, message = "A quantidade máxima por encomenda é 20")
    @Column (nullable = false)
    private Short quantidade;

    @NotNull (message = "A data de retirada é obrigatória")
    @FutureOrPresent (message = "A data de retirada não pode estar no passado")
    @Column (nullable = false)
    private LocalDate dataRetirada;

    @NotNull (message = "O horário de retirada é obrigatório")
    @JsonFormat (pattern = "HH:mm")
    @Column (nullable = false)
    private LocalTime horaRetirada;

    @Size (max = 255, message = "A observação deve ter no máximo 255 caracteres")
    private String observacao;

    // Campos abaixo são calculados pelo serviço (o cliente não envia)
    @JsonProperty (access = JsonProperty.Access.READ_ONLY)
    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 10)
    private StatusEncomenda status;

    @JsonProperty (access = JsonProperty.Access.READ_ONLY)
    @Column (nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @JsonProperty (access = JsonProperty.Access.READ_ONLY)
    @Column (nullable = false)
    private LocalDateTime dataCriacao;

    // Obrigatório só no cadastro (conferido no serviço): na edição o produto não muda e não precisa ser enviado
    @ManyToOne (optional = false)
    private Produto produto;
}
