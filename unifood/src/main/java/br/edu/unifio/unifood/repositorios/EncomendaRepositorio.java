package br.edu.unifio.unifood.repositorios;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.unifio.unifood.entidades.Encomenda;
import br.edu.unifio.unifood.entidades.StatusEncomenda;

public interface EncomendaRepositorio extends JpaRepository<Encomenda, Integer> {
    List<Encomenda> findByProdutoVendedorIdOrderByDataRetiradaAscHoraRetiradaAsc (Integer vendedorId);

    List<Encomenda> findByStatusOrderByDataRetiradaAscHoraRetiradaAsc (StatusEncomenda status);

    boolean existsByProdutoIdAndStatusIn (Integer produtoId, Collection<StatusEncomenda> status);

    boolean existsByProdutoVendedorIdAndStatusIn (Integer vendedorId, Collection<StatusEncomenda> status);

    long countByProdutoVendedorIdAndStatus (Integer vendedorId, StatusEncomenda status);

    @Query ("select sum(e.valorTotal) from Encomenda e where e.produto.vendedor.id = :vendedorId and e.status = :status")
    BigDecimal somarValorPorVendedorEStatus (@Param ("vendedorId") Integer vendedorId, @Param ("status") StatusEncomenda status);

    void deleteByProdutoId (Integer produtoId);
}
