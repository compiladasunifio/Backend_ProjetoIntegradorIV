package br.edu.unifio.unifood.repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.unifood.entidades.Produto;

// Usa os dados do import.sql. @Transactional desfaz as alterações ao fim de cada teste.
@SpringBootTest
@Transactional
public class ProdutoRepositorioTests {
    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @Autowired
    private VendedorRepositorio vendedorRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Test
    public void deveBuscarTodosOsProdutosOrdenadosPorNome () {
        List<Produto> produtos = produtoRepositorio.findAll(Sort.by("nome"));

        assertEquals(6, produtos.size());
        assertEquals("Bolo de pote de ninho", produtos.get(0).getNome());
        assertEquals("Brigadeiro gourmet", produtos.get(1).getNome());
    }

    @Test
    public void deveBuscarUmProdutoPorId () {
        Produto produto = produtoRepositorio.findById(4).orElseThrow();

        assertEquals("Marmita fitness", produto.getNome());
        assertEquals("Fit Express", produto.getVendedor().getNomeNegocio());
    }

    @Test
    public void deveSalvarUmProdutoNovo () {
        Produto produto = new Produto();
        produto.setNome("Esfirra de carne");
        produto.setDescricao("Esfirra aberta assada");
        produto.setPreco(new BigDecimal("6.50"));
        produto.setDisponivel(true);
        produto.setLocalizacao("Bloco 6");
        produto.setVendedor(vendedorRepositorio.findById(3).orElseThrow());
        produto.setCategoria(categoriaRepositorio.findById(Short.parseShort("2")).orElseThrow());

        produtoRepositorio.save(produto);

        assertNotNull(produto.getId());
        assertEquals(7, produtoRepositorio.count());
    }

    @Test
    public void deveExcluirUmProdutoSemDependencias () {
        // Produto 6 (suco) não tem avaliações nem encomendas
        assertTrue(produtoRepositorio.existsById(6));
        produtoRepositorio.deleteById(6);
        assertFalse(produtoRepositorio.existsById(6));
    }

    @Test
    public void deveListarSomenteOsDisponiveis () {
        List<Produto> disponiveis = produtoRepositorio.findByDisponivelTrueOrderByNome();

        assertEquals(5, disponiveis.size());
        assertTrue(disponiveis.stream().allMatch(Produto::getDisponivel));
    }

    @Test
    public void deveBuscarPorNomeIgnorandoMaiusculas () {
        List<Produto> produtos = produtoRepositorio.findByNomeContainingIgnoreCaseOrderByNome("BRIGA");

        assertEquals(1, produtos.size());
        assertEquals("Brigadeiro gourmet", produtos.get(0).getNome());
    }

    @Test
    public void deveBuscarDisponiveisPorFaixaDePreco () {
        // A coxinha (6,00) está indisponível e não deve aparecer
        List<Produto> produtos = produtoRepositorio.buscarDisponiveisPorFaixaDePreco(new BigDecimal("5"), new BigDecimal("12"));

        assertEquals(4, produtos.size());
        assertEquals("Suco natural de laranja", produtos.get(0).getNome());
        assertEquals("Brigadeiro gourmet", produtos.get(3).getNome());
    }
}
