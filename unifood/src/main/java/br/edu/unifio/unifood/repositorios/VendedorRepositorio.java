package br.edu.unifio.unifood.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.unifio.unifood.entidades.Vendedor;

public interface VendedorRepositorio extends JpaRepository <Vendedor, Integer> {
    // Regra: o e-mail do vendedor é único
    boolean existsByEmailIgnoreCase (String email);

    boolean existsByEmailIgnoreCaseAndIdNot (String email, Integer id);

    // Busca de vendedores pelo nome do negócio (ex.: "doces")
    List<Vendedor> findByNomeNegocioContainingIgnoreCaseOrderByNomeNegocio (String nomeNegocio);
}
