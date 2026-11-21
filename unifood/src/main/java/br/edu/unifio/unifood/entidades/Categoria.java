package br.edu.unifio.unifood.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Categoria {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Short id;

    @NotBlank (message = "O nome da categoria é obrigatório")
    @Size (max = 50, message = "O nome da categoria deve ter no máximo 50 caracteres")
    @Column (nullable = false, unique = true, length = 50)
    private String nome;

    @Size (max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String descricao;
}
