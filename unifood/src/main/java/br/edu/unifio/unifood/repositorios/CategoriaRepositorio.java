package br.edu.unifio.unifood.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.unifio.unifood.entidades.Categoria;

public interface CategoriaRepositorio extends JpaRepository <Categoria, Short> {
    boolean existsByNomeIgnoreCase (String nome);

    boolean existsByNomeIgnoreCaseAndIdNot (String nome, Short id);
}
