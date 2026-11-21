package br.edu.unifio.unifood.recursos;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.unifio.unifood.entidades.Avaliacao;
import br.edu.unifio.unifood.entidades.Produto;
import br.edu.unifio.unifood.servicos.AvaliacaoServico;
import br.edu.unifio.unifood.servicos.ProdutoServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping ("produtos")
public class ProdutoRecurso {
    @Autowired
    private ProdutoServico produtoServico;

    @Autowired
    private AvaliacaoServico avaliacaoServico;

    @GetMapping
    public List<Produto> listar () {
        return produtoServico.listar();
    }

    @GetMapping ("disponiveis")
    public List<Produto> listarDisponiveis () {
        return produtoServico.listarDisponiveis();
    }

    @GetMapping ("busca")
    public List<Produto> buscarPorNome (@RequestParam String nome) {
        return produtoServico.buscarPorNome(nome);
    }

    @GetMapping ("faixa-preco")
    public List<Produto> buscarPorFaixaDePreco (@RequestParam BigDecimal minimo, @RequestParam BigDecimal maximo) {
        return produtoServico.buscarPorFaixaDePreco(minimo, maximo);
    }

    @GetMapping ("{id}")
    public Produto buscarPorId (@PathVariable Integer id) {
        return produtoServico.buscarPorId(id);
    }

    @GetMapping ("{id}/avaliacoes")
    public List<Avaliacao> listarAvaliacoes (@PathVariable Integer id) {
        return avaliacaoServico.listarPorProduto(id);
    }

    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public Produto salvar (@Valid @RequestBody Produto produto) {
        return produtoServico.salvar(produto);
    }

    @PutMapping ("{id}")
    public Produto atualizar (@PathVariable Integer id, @Valid @RequestBody Produto produto) {
        return produtoServico.atualizar(id, produto);
    }

    @PatchMapping ("{id}/disponibilidade")
    public Produto alterarDisponibilidade (@PathVariable Integer id, @RequestParam boolean disponivel) {
        return produtoServico.alterarDisponibilidade(id, disponivel);
    }

    @DeleteMapping ("{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void excluirPorId (@PathVariable Integer id) {
        produtoServico.excluirPorId(id);
    }
}
