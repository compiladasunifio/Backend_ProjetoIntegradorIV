package br.edu.unifio.unifood.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Vendedor {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank (message = "O nome é obrigatório")
    @Size (max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Column (nullable = false, length = 100)
    private String nome;

    @NotBlank (message = "O e-mail é obrigatório")
    @Email (message = "Informe um e-mail válido")
    @Size (max = 120, message = "O e-mail deve ter no máximo 120 caracteres")
    @Column (nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank (message = "O telefone é obrigatório")
    @Pattern (regexp = "\\d{10,11}", message = "O telefone deve ter 10 ou 11 dígitos (DDD + número), somente números")
    @Column (nullable = false, length = 11)
    private String telefone;

    @NotBlank (message = "O nome do negócio é obrigatório")
    @Size (max = 100, message = "O nome do negócio deve ter no máximo 100 caracteres")
    @Column (nullable = false, length = 100)
    private String nomeNegocio;

    @NotBlank (message = "A localização é obrigatória")
    @Size (max = 100, message = "A localização deve ter no máximo 100 caracteres")
    @Column (nullable = false, length = 100)
    private String localizacao;
}
