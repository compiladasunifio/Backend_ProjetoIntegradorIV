package br.edu.unifio.unifood.servicos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.unifood.entidades.DiaSemana;
import br.edu.unifio.unifood.entidades.Encomenda;
import br.edu.unifio.unifood.entidades.Produto;
import br.edu.unifio.unifood.entidades.StatusEncomenda;
import br.edu.unifio.unifood.entidades.Vendedor;
import br.edu.unifio.unifood.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.unifood.excecoes.RegraNegocioExcecao;
import br.edu.unifio.unifood.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.unifood.repositorios.EncomendaRepositorio;
import br.edu.unifio.unifood.repositorios.HorarioAtendimentoRepositorio;

// Funcionalidade específica do PI: encomenda de produtos para retirada no campus.
@Service
public class EncomendaServico {
    @Autowired
    private EncomendaRepositorio encomendaRepositorio;

    @Autowired
    private HorarioAtendimentoRepositorio horarioRepositorio;

    @Autowired
    private ProdutoServico produtoServico;

    @Autowired
    private VendedorServico vendedorServico;

    public List<Encomenda> listar () {
        return encomendaRepositorio.findAll(Sort.by("dataRetirada", "horaRetirada"));
    }

    public List<Encomenda> listarPorStatus (StatusEncomenda status) {
        return encomendaRepositorio.findByStatusOrderByDataRetiradaAscHoraRetiradaAsc(status);
    }

    // Agenda de retiradas do vendedor
    public List<Encomenda> listarPorVendedor (Integer vendedorId) {
        vendedorServico.buscarPorId(vendedorId);
        return encomendaRepositorio.findByProdutoVendedorIdOrderByDataRetiradaAscHoraRetiradaAsc(vendedorId);
    }

    public Encomenda buscarPorId (Integer id) {
        return encomendaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Encomenda de id " + id + " não encontrada"));
    }

    @Transactional
    public Encomenda salvar (Encomenda encomenda) {
        if (encomenda.getProduto().getId() == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do produto. Ex.: \"produto\": {\"id\": 1}");
        }
        Produto produto = produtoServico.buscarPorId(encomenda.getProduto().getId());

        // Regra 1: produto indisponível não aceita encomenda
        if (!Boolean.TRUE.equals(produto.getDisponivel())) {
            throw new RegraNegocioExcecao("O produto '" + produto.getNome() + "' está indisponível no momento e não aceita encomendas");
        }
        // Regra 2: a retirada precisa cair em um horário de atendimento do vendedor
        validarRetirada(produto.getVendedor(), encomenda.getDataRetirada(), encomenda.getHoraRetirada());

        encomenda.setId(null);
        encomenda.setProduto(produto);
        encomenda.setStatus(StatusEncomenda.PENDENTE);
        encomenda.setDataCriacao(LocalDateTime.now());
        // Regra 3: o valor total é calculado pelo servidor (preço atual x quantidade)
        encomenda.setValorTotal(calcularValor(produto, encomenda.getQuantidade()));
        return encomendaRepositorio.save(encomenda);
    }

    // Regra: só dá para alterar a encomenda enquanto ela está PENDENTE (o produto não muda)
    @Transactional
    public Encomenda atualizar (Integer id, Encomenda dados) {
        Encomenda encomenda = buscarPorId(id);
        if (encomenda.getStatus() != StatusEncomenda.PENDENTE) {
            throw new RegraNegocioExcecao("A encomenda " + id + " está " + encomenda.getStatus()
                    + " e não pode mais ser alterada. Só encomendas PENDENTES podem ser editadas");
        }
        validarRetirada(encomenda.getProduto().getVendedor(), dados.getDataRetirada(), dados.getHoraRetirada());

        encomenda.setClienteNome(dados.getClienteNome());
        encomenda.setClienteTelefone(dados.getClienteTelefone());
        encomenda.setQuantidade(dados.getQuantidade());
        encomenda.setDataRetirada(dados.getDataRetirada());
        encomenda.setHoraRetirada(dados.getHoraRetirada());
        encomenda.setObservacao(dados.getObservacao());
        encomenda.setValorTotal(calcularValor(encomenda.getProduto(), dados.getQuantidade()));
        return encomendaRepositorio.save(encomenda);
    }

    // Vendedor aceita a encomenda
    @Transactional
    public Encomenda confirmar (Integer id) {
        return mudarStatus(id, StatusEncomenda.CONFIRMADA);
    }

    // Vendedor entregou o produto ao cliente
    @Transactional
    public Encomenda entregar (Integer id) {
        return mudarStatus(id, StatusEncomenda.ENTREGUE);
    }

    // Cliente ou vendedor desiste da encomenda
    @Transactional
    public Encomenda cancelar (Integer id) {
        return mudarStatus(id, StatusEncomenda.CANCELADA);
    }

    // Regra: só encomendas finalizadas (entregues ou canceladas) podem ser apagadas
    @Transactional
    public void excluirPorId (Integer id) {
        Encomenda encomenda = buscarPorId(id);
        if (encomenda.getStatus().emAberto()) {
            throw new RegraNegocioExcecao("A encomenda " + id + " ainda está " + encomenda.getStatus()
                    + ". Cancele ou entregue a encomenda antes de excluí-la");
        }
        encomendaRepositorio.delete(encomenda);
    }

    private Encomenda mudarStatus (Integer id, StatusEncomenda novoStatus) {
        Encomenda encomenda = buscarPorId(id);
        // Regra: respeitar o fluxo PENDENTE -> CONFIRMADA -> ENTREGUE (ou CANCELADA)
        if (!encomenda.getStatus().podeMudarPara(novoStatus)) {
            throw new RegraNegocioExcecao("Não é possível mudar a encomenda " + id + " de "
                    + encomenda.getStatus() + " para " + novoStatus);
        }
        encomenda.setStatus(novoStatus);
        return encomendaRepositorio.save(encomenda);
    }

    private void validarRetirada (Vendedor vendedor, LocalDate data, LocalTime hora) {
        DiaSemana dia = DiaSemana.de(data.getDayOfWeek());
        if (dia == null) {
            throw new RegraNegocioExcecao("Não há atendimento no campus aos domingos. Escolha outra data de retirada");
        }
        boolean atende = horarioRepositorio
                .existsByVendedorIdAndDiaAndInicioLessThanEqualAndFimGreaterThanEqual(vendedor.getId(), dia, hora, hora);
        if (!atende) {
            throw new RegraNegocioExcecao("O vendedor '" + vendedor.getNomeNegocio() + "' não atende em " + dia
                    + " às " + hora + ". Consulte os horários em /vendedores/" + vendedor.getId() + "/horarios");
        }
    }

    private BigDecimal calcularValor (Produto produto, Short quantidade) {
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }
}
