package br.edu.unifio.unifood.entidades;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Produto {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank (message = "O nome do produto é obrigatório")
    @Size (max = 100, message = "O nome do produto deve ter no máximo 100 caracteres")
    @Column (nullable = false, length = 100)
    private String nome;

    @Size (max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Column (length = 500)
    private String descricao;

    @NotNull (message = "O preço é obrigatório")
    @DecimalMin (value = "0.01", message = "O preço deve ser maior que zero")
    @Digits (integer = 6, fraction = 2, message = "O preço deve ter no máximo 6 dígitos inteiros e 2 casas decimais")
    @Column (nullable = false, precision = 8, scale = 2)
    private BigDecimal preco;

    @Size (max = 500, message = "A URL da imagem deve ter no máximo 500 caracteres")
    @Column (length = 500)
    private String imagemUrl;

    @NotNull (message = "Informe se o produto está disponível")
    @Column (nullable = false)
    private Boolean disponivel = true;

    @Size (max = 100, message = "A localização deve ter no máximo 100 caracteres")
    @Column (nullable = false, length = 100)
    private String localizacao;

    @NotNull (message = "O vendedor é obrigatório")
    @ManyToOne (optional = false)
    private Vendedor vendedor;

    @NotNull (message = "A categoria é obrigatória")
    @ManyToOne (optional = false)
    private Categoria categoria;
}
