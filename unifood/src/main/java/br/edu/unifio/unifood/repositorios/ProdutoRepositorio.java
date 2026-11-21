package br.edu.unifio.unifood.repositorios;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.unifio.unifood.entidades.Produto;

public interface ProdutoRepositorio extends JpaRepository<Produto, Integer> {
    List<Produto> findByDisponivelTrueOrderByNome ();

    List<Produto> findByNomeContainingIgnoreCaseOrderByNome (String nome);

    List<Produto> findByVendedorIdOrderByNome (Integer vendedorId);

    List<Produto> findByCategoriaIdOrderByNome (Short categoriaId);

    @Query ("select p from Produto p where p.disponivel = true and p.preco between :minimo and :maximo order by p.preco, p.nome")
    List<Produto> buscarDisponiveisPorFaixaDePreco (@Param ("minimo") BigDecimal minimo, @Param ("maximo") BigDecimal maximo);

    boolean existsByCategoriaId (Short categoriaId);

    long countByVendedorId (Integer vendedorId);

    long countByVendedorIdAndDisponivelTrue (Integer vendedorId);
}
