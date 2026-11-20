package br.edu.unifio.unifood.servicos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.unifood.entidades.Avaliacao;
import br.edu.unifio.unifood.entidades.Produto;
import br.edu.unifio.unifood.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.unifood.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.unifood.repositorios.AvaliacaoRepositorio;

@Service
public class AvaliacaoServico {
    @Autowired
    private AvaliacaoRepositorio avaliacaoRepositorio;

    @Autowired
    private ProdutoServico produtoServico;

    public List<Avaliacao> listar () {
        return avaliacaoRepositorio.findAll(Sort.by(Sort.Direction.DESC, "data", "id"));
    }

    public List<Avaliacao> listarPorProduto (Integer produtoId) {
        produtoServico.buscarPorId(produtoId);
        return avaliacaoRepositorio.findByProdutoIdOrderByDataDescIdDesc(produtoId);
    }

    public Avaliacao buscarPorId (Integer id) {
        return avaliacaoRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Avaliação de id " + id + " não encontrada"));
    }

    @Transactional
    public Avaliacao salvar (Avaliacao avaliacao) {
        if (avaliacao.getProduto() == null || avaliacao.getProduto().getId() == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do produto. Ex.: \"produto\": {\"id\": 1}");
        }
        Produto produto = produtoServico.buscarPorId(avaliacao.getProduto().getId());
        avaliacao.setId(null);
        avaliacao.setProduto(produto);
        // A data é sempre a do dia da publicação
        avaliacao.setData(LocalDate.now());
        return avaliacaoRepositorio.save(avaliacao);
    }

    // Na edição só mudam autor, nota e comentário (o produto e a data ficam os mesmos)
    @Transactional
    public Avaliacao atualizar (Integer id, Avaliacao dados) {
        Avaliacao avaliacao = buscarPorId(id);
        avaliacao.setAutorNome(dados.getAutorNome());
        avaliacao.setNota(dados.getNota());
        avaliacao.setComentario(dados.getComentario());
        return avaliacaoRepositorio.save(avaliacao);
    }

    @Transactional
    public void excluirPorId (Integer id) {
        avaliacaoRepositorio.delete(buscarPorId(id));
    }
}
