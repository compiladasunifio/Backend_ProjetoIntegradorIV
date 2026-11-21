package br.edu.unifio.unifood.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.unifood.entidades.Categoria;
import br.edu.unifio.unifood.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.unifood.excecoes.RegraNegocioExcecao;
import br.edu.unifio.unifood.repositorios.CategoriaRepositorio;
import br.edu.unifio.unifood.repositorios.ProdutoRepositorio;

@Service
public class CategoriaServico {
    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    public List<Categoria> listar () {
        return categoriaRepositorio.findAll(Sort.by("nome"));
    }

    public Categoria buscarPorId (Short id) {
        return categoriaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Categoria de id " + id + " não encontrada"));
    }

    @Transactional
    public Categoria salvar (Categoria categoria) {
        categoria.setId(null);
        if (categoriaRepositorio.existsByNomeIgnoreCase(categoria.getNome())) {
            throw new RegraNegocioExcecao("Já existe uma categoria com o nome '" + categoria.getNome() + "'");
        }
        return categoriaRepositorio.save(categoria);
    }

    @Transactional
    public Categoria atualizar (Short id, Categoria dados) {
        Categoria categoria = buscarPorId(id);
        if (categoriaRepositorio.existsByNomeIgnoreCaseAndIdNot(dados.getNome(), id)) {
            throw new RegraNegocioExcecao("Já existe uma categoria com o nome '" + dados.getNome() + "'");
        }
        categoria.setNome(dados.getNome());
        categoria.setDescricao(dados.getDescricao());
        return categoriaRepositorio.save(categoria);
    }

    @Transactional
    public void excluirPorId (Short id) {
        Categoria categoria = buscarPorId(id);
        if (produtoRepositorio.existsByCategoriaId(id)) {
            throw new RegraNegocioExcecao("A categoria '" + categoria.getNome()
                    + "' possui produtos cadastrados. Mova ou exclua os produtos antes de excluir a categoria");
        }
        categoriaRepositorio.delete(categoria);
    }
}
