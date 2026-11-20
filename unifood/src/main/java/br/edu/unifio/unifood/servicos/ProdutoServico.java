package br.edu.unifio.unifood.servicos;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.unifood.entidades.Categoria;
import br.edu.unifio.unifood.entidades.Produto;
import br.edu.unifio.unifood.entidades.StatusEncomenda;
import br.edu.unifio.unifood.entidades.Vendedor;
import br.edu.unifio.unifood.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.unifood.excecoes.RegraNegocioExcecao;
import br.edu.unifio.unifood.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.unifood.repositorios.AvaliacaoRepositorio;
import br.edu.unifio.unifood.repositorios.EncomendaRepositorio;
import br.edu.unifio.unifood.repositorios.ProdutoRepositorio;

@Service
public class ProdutoServico {
    private static final List<StatusEncomenda> EM_ABERTO = List.of(StatusEncomenda.PENDENTE, StatusEncomenda.CONFIRMADA);

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private AvaliacaoRepositorio avaliacaoRepositorio;

    @Autowired
    private EncomendaRepositorio encomendaRepositorio;

    @Autowired
    private VendedorServico vendedorServico;

    @Autowired
    private CategoriaServico categoriaServico;

    public List<Produto> listar () {
        return produtoRepositorio.findAll(Sort.by("nome"));
    }

    public List<Produto> listarDisponiveis () {
        return produtoRepositorio.findByDisponivelTrueOrderByNome();
    }

    public List<Produto> buscarPorNome (String nome) {
        return produtoRepositorio.findByNomeContainingIgnoreCaseOrderByNome(nome);
    }

    public List<Produto> buscarPorFaixaDePreco (BigDecimal minimo, BigDecimal maximo) {
        if (minimo.compareTo(BigDecimal.ZERO) < 0 || maximo.compareTo(minimo) < 0) {
            throw new RequisicaoInvalidaExcecao("Faixa de preço inválida: o mínimo deve ser maior ou igual a zero e o máximo maior ou igual ao mínimo");
        }
        return produtoRepositorio.buscarDisponiveisPorFaixaDePreco(minimo, maximo);
    }

    public List<Produto> listarPorVendedor (Integer vendedorId) {
        vendedorServico.buscarPorId(vendedorId);
        return produtoRepositorio.findByVendedorIdOrderByNome(vendedorId);
    }

    public List<Produto> listarPorCategoria (Short categoriaId) {
        categoriaServico.buscarPorId(categoriaId);
        return produtoRepositorio.findByCategoriaIdOrderByNome(categoriaId);
    }

    public Produto buscarPorId (Integer id) {
        return produtoRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Produto de id " + id + " não encontrado"));
    }

    @Transactional
    public Produto salvar (Produto produto) {
        produto.setId(null);
        preencherRelacionamentos(produto, produto);
        return produtoRepositorio.save(produto);
    }

    @Transactional
    public Produto atualizar (Integer id, Produto dados) {
        Produto produto = buscarPorId(id);
        produto.setNome(dados.getNome());
        produto.setDescricao(dados.getDescricao());
        produto.setPreco(dados.getPreco());
        produto.setImagemUrl(dados.getImagemUrl());
        produto.setDisponivel(dados.getDisponivel());
        produto.setLocalizacao(dados.getLocalizacao());
        preencherRelacionamentos(produto, dados);
        return produtoRepositorio.save(produto);
    }

    // Chave "disponível / indisponível" do card em "Meus produtos"
    @Transactional
    public Produto alterarDisponibilidade (Integer id, boolean disponivel) {
        Produto produto = buscarPorId(id);
        produto.setDisponivel(disponivel);
        return produtoRepositorio.save(produto);
    }

    // Excluir um produto apaga as avaliações dele e o histórico de encomendas finalizadas.
    // Regra: não pode excluir produto com encomendas pendentes ou confirmadas.
    @Transactional
    public void excluirPorId (Integer id) {
        Produto produto = buscarPorId(id);
        if (encomendaRepositorio.existsByProdutoIdAndStatusIn(id, EM_ABERTO)) {
            throw new RegraNegocioExcecao("O produto '" + produto.getNome()
                    + "' possui encomendas pendentes ou confirmadas. Entregue ou cancele as encomendas antes de excluir o produto");
        }
        encomendaRepositorio.deleteByProdutoId(id);
        avaliacaoRepositorio.deleteByProdutoId(id);
        produtoRepositorio.delete(produto);
    }

    // Troca os objetos {"id": X} enviados no JSON pelos registros do banco (404 se não existirem)
    private void preencherRelacionamentos (Produto produto, Produto dados) {
        if (dados.getVendedor().getId() == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do vendedor. Ex.: \"vendedor\": {\"id\": 1}");
        }
        if (dados.getCategoria().getId() == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id da categoria. Ex.: \"categoria\": {\"id\": 1}");
        }
        Vendedor vendedor = vendedorServico.buscarPorId(dados.getVendedor().getId());
        Categoria categoria = categoriaServico.buscarPorId(dados.getCategoria().getId());
        produto.setVendedor(vendedor);
        produto.setCategoria(categoria);

        // Regra: sem localização informada, o produto fica onde o vendedor costuma ficar no campus
        if (produto.getLocalizacao() == null || produto.getLocalizacao().isBlank()) {
            produto.setLocalizacao(vendedor.getLocalizacao());
        }
    }
}
