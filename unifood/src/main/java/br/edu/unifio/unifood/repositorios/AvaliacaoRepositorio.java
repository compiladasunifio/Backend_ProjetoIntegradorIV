package br.edu.unifio.unifood.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.unifio.unifood.entidades.Avaliacao;

public interface AvaliacaoRepositorio extends JpaRepository<Avaliacao, Integer> {
    // Avaliações de um produto, das mais recentes para as mais antigas
    List<Avaliacao> findByProdutoIdOrderByDataDescIdDesc (Integer produtoId);

    // Consulta JPQL: nota média de todos os produtos de um vendedor (Painel)
    @Query ("select avg(a.nota) from Avaliacao a where a.produto.vendedor.id = :vendedorId")
    Double calcularMediaPorVendedor (@Param ("vendedorId") Integer vendedorId);

    // Consulta JPQL: nota média de um produto
    @Query ("select avg(a.nota) from Avaliacao a where a.produto.id = :produtoId")
    Double calcularMediaPorProduto (@Param ("produtoId") Integer produtoId);

    long countByProdutoVendedorId (Integer vendedorId);

    void deleteByProdutoId (Integer produtoId);
}
