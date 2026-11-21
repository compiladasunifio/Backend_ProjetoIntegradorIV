package br.edu.unifio.unifood.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.unifio.unifood.entidades.Avaliacao;

public interface AvaliacaoRepositorio extends JpaRepository<Avaliacao, Integer> {
    List<Avaliacao> findByProdutoIdOrderByDataDescIdDesc (Integer produtoId);

    @Query ("select avg(a.nota) from Avaliacao a where a.produto.vendedor.id = :vendedorId")
    Double calcularMediaPorVendedor (@Param ("vendedorId") Integer vendedorId);

    @Query ("select avg(a.nota) from Avaliacao a where a.produto.id = :produtoId")
    Double calcularMediaPorProduto (@Param ("produtoId") Integer produtoId);

    long countByProdutoVendedorId (Integer vendedorId);

    void deleteByProdutoId (Integer produtoId);
}
