package br.edu.unifio.unifood.servicos;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.unifood.dtos.PainelVendedor;
import br.edu.unifio.unifood.entidades.HorarioAtendimento;
import br.edu.unifio.unifood.entidades.Produto;
import br.edu.unifio.unifood.entidades.StatusEncomenda;
import br.edu.unifio.unifood.entidades.Vendedor;
import br.edu.unifio.unifood.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.unifood.excecoes.RegraNegocioExcecao;
import br.edu.unifio.unifood.repositorios.AvaliacaoRepositorio;
import br.edu.unifio.unifood.repositorios.EncomendaRepositorio;
import br.edu.unifio.unifood.repositorios.HorarioAtendimentoRepositorio;
import br.edu.unifio.unifood.repositorios.ProdutoRepositorio;
import br.edu.unifio.unifood.repositorios.VendedorRepositorio;

@Service
public class VendedorServico {
    private static final List<StatusEncomenda> EM_ABERTO = List.of(StatusEncomenda.PENDENTE, StatusEncomenda.CONFIRMADA);

    @Autowired
    private VendedorRepositorio vendedorRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private AvaliacaoRepositorio avaliacaoRepositorio;

    @Autowired
    private HorarioAtendimentoRepositorio horarioRepositorio;

    @Autowired
    private EncomendaRepositorio encomendaRepositorio;

    public List<Vendedor> listar () {
        return vendedorRepositorio.findAll(Sort.by("nomeNegocio"));
    }

    public List<Vendedor> buscarPorNomeNegocio (String nomeNegocio) {
        return vendedorRepositorio.findByNomeNegocioContainingIgnoreCaseOrderByNomeNegocio(nomeNegocio);
    }

    public Vendedor buscarPorId (Integer id) {
        return vendedorRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Vendedor de id " + id + " não encontrado"));
    }

    @Transactional
    public Vendedor salvar (Vendedor vendedor) {
        vendedor.setId(null);
        vendedor.setEmail(vendedor.getEmail().trim().toLowerCase());
        // Regra: e-mail único entre os vendedores
        if (vendedorRepositorio.existsByEmailIgnoreCase(vendedor.getEmail())) {
            throw new RegraNegocioExcecao("Já existe um vendedor cadastrado com o e-mail " + vendedor.getEmail());
        }
        return vendedorRepositorio.save(vendedor);
    }

    @Transactional
    public Vendedor atualizar (Integer id, Vendedor dados) {
        Vendedor vendedor = buscarPorId(id);
        String email = dados.getEmail().trim().toLowerCase();
        if (vendedorRepositorio.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new RegraNegocioExcecao("O e-mail " + email + " já pertence a outro vendedor");
        }
        vendedor.setNome(dados.getNome());
        vendedor.setEmail(email);
        vendedor.setTelefone(dados.getTelefone());
        vendedor.setNomeNegocio(dados.getNomeNegocio());
        vendedor.setLocalizacao(dados.getLocalizacao());
        return vendedorRepositorio.save(vendedor);
    }

    // Excluir a conta apaga junto os produtos (com avaliações e encomendas finalizadas) e os horários.
    // Regra: não pode excluir se houver encomendas pendentes ou confirmadas para entregar.
    @Transactional
    public void excluirPorId (Integer id) {
        Vendedor vendedor = buscarPorId(id);
        if (encomendaRepositorio.existsByProdutoVendedorIdAndStatusIn(id, EM_ABERTO)) {
            throw new RegraNegocioExcecao("O vendedor '" + vendedor.getNomeNegocio()
                    + "' possui encomendas pendentes ou confirmadas. Entregue ou cancele as encomendas antes de excluir a conta");
        }
        for (Produto produto : produtoRepositorio.findByVendedorIdOrderByNome(id)) {
            encomendaRepositorio.deleteByProdutoId(produto.getId());
            avaliacaoRepositorio.deleteByProdutoId(produto.getId());
            produtoRepositorio.delete(produto);
        }
        horarioRepositorio.deleteByVendedorId(id);
        vendedorRepositorio.delete(vendedor);
    }

    // Funcionalidade do PI: painel com os indicadores do negócio do vendedor
    public PainelVendedor gerarPainel (Integer id) {
        Vendedor vendedor = buscarPorId(id);

        Double media = avaliacaoRepositorio.calcularMediaPorVendedor(id);
        Double notaMedia = media == null ? null : Math.round(media * 10) / 10.0;

        long minutosSemana = 0;
        for (HorarioAtendimento horario : horarioRepositorio.findByVendedorId(id)) {
            minutosSemana += Duration.between(horario.getInicio(), horario.getFim()).toMinutes();
        }
        double horasSemana = Math.round(minutosSemana / 60.0 * 10) / 10.0;

        BigDecimal faturamento = encomendaRepositorio.somarValorPorVendedorEStatus(id, StatusEncomenda.ENTREGUE);
        if (faturamento == null) {
            faturamento = BigDecimal.ZERO;
        }

        return new PainelVendedor(
                vendedor.getId(),
                vendedor.getNomeNegocio(),
                produtoRepositorio.countByVendedorId(id),
                produtoRepositorio.countByVendedorIdAndDisponivelTrue(id),
                notaMedia,
                avaliacaoRepositorio.countByProdutoVendedorId(id),
                horasSemana,
                encomendaRepositorio.countByProdutoVendedorIdAndStatus(id, StatusEncomenda.PENDENTE),
                encomendaRepositorio.countByProdutoVendedorIdAndStatus(id, StatusEncomenda.CONFIRMADA),
                encomendaRepositorio.countByProdutoVendedorIdAndStatus(id, StatusEncomenda.ENTREGUE),
                faturamento);
    }
}
