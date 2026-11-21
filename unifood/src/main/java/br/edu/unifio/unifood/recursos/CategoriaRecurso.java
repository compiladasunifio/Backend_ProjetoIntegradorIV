package br.edu.unifio.unifood.recursos;

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

import br.edu.unifio.unifood.entidades.Categoria;
import br.edu.unifio.unifood.entidades.Produto;
import br.edu.unifio.unifood.servicos.CategoriaServico;
import br.edu.unifio.unifood.servicos.ProdutoServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping ("categorias")
public class CategoriaRecurso {
    @Autowired
    private CategoriaServico categoriaServico;

    @Autowired
    private ProdutoServico produtoServico;

    @GetMapping
    public List<Categoria> listar () {
        return categoriaServico.listar();
    }

    @GetMapping ("{id}")
    public Categoria buscarPorId (@PathVariable Short id) {
        return categoriaServico.buscarPorId(id);
    }

    @GetMapping ("{id}/produtos")
    public List<Produto> listarProdutos (@PathVariable Short id) {
        return produtoServico.listarPorCategoria(id);
    }

    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public Categoria salvar (@Valid @RequestBody Categoria categoria) {
        return categoriaServico.salvar(categoria);
    }

    @PutMapping ("{id}")
    public Categoria atualizar (@PathVariable Short id, @Valid @RequestBody Categoria categoria) {
        return categoriaServico.atualizar(id, categoria);
    }

    @DeleteMapping ("{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void excluirPorId (@PathVariable Short id) {
        categoriaServico.excluirPorId(id);
    }
}
