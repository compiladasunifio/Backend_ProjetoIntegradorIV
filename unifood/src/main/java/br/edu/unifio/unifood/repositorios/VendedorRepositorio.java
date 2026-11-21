package br.edu.unifio.unifood.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.unifio.unifood.entidades.Vendedor;

public interface VendedorRepositorio extends JpaRepository <Vendedor, Integer> {
    boolean existsByEmailIgnoreCase (String email);

    boolean existsByEmailIgnoreCaseAndIdNot (String email, Integer id);

    List<Vendedor> findByNomeNegocioContainingIgnoreCaseOrderByNomeNegocio (String nomeNegocio);
}
