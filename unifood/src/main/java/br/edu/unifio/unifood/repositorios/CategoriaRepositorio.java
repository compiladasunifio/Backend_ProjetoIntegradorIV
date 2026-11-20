package br.edu.unifio.unifood.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.unifio.unifood.entidades.Categoria;

public interface CategoriaRepositorio extends JpaRepository <Categoria, Short> {
    // Usado para não deixar cadastrar duas categorias com o mesmo nome
    boolean existsByNomeIgnoreCase (String nome);

    boolean existsByNomeIgnoreCaseAndIdNot (String nome, Short id);
}
