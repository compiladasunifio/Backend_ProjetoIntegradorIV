package br.edu.unifio.unifood.servicos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.unifood.entidades.Encomenda;
import br.edu.unifio.unifood.entidades.Produto;
import br.edu.unifio.unifood.entidades.StatusEncomenda;
import br.edu.unifio.unifood.excecoes.RegraNegocioExcecao;

@SpringBootTest
@Transactional
public class EncomendaServicoTests {
    @Autowired
    private EncomendaServico encomendaServico;

    private Encomenda novaEncomenda (Integer produtoId, DayOfWeek dia, LocalTime hora, int quantidade) {
        Produto produto = new Produto();
        produto.setId(produtoId);

        Encomenda encomenda = new Encomenda();
        encomenda.setClienteNome("Cliente Teste");
        encomenda.setClienteTelefone("14990000000");
        encomenda.setQuantidade((short) quantidade);
        encomenda.setDataRetirada(LocalDate.now().with(TemporalAdjusters.next(dia)));
        encomenda.setHoraRetirada(hora);
        encomenda.setProduto(produto);
        return encomenda;
    }

    @Test
    public void deveCriarEncomendaPendenteECalcularOValor () {
        Encomenda encomenda = encomendaServico.salvar(novaEncomenda(1, DayOfWeek.MONDAY, LocalTime.of(10, 30), 2));

        assertEquals(StatusEncomenda.PENDENTE, encomenda.getStatus());
        assertEquals(0, new BigDecimal("24.00").compareTo(encomenda.getValorTotal()));
    }

    @Test
    public void naoDeveEncomendarProdutoIndisponivel () {
        assertThrows(RegraNegocioExcecao.class,
                () -> encomendaServico.salvar(novaEncomenda(3, DayOfWeek.THURSDAY, LocalTime.of(19, 0), 1)));
    }

    @Test
    public void naoDeveEncomendarForaDoHorarioDeAtendimento () {
        assertThrows(RegraNegocioExcecao.class,
                () -> encomendaServico.salvar(novaEncomenda(1, DayOfWeek.MONDAY, LocalTime.of(15, 0), 1)));
    }

    @Test
    public void naoDeveEncomendarParaDomingo () {
        assertThrows(RegraNegocioExcecao.class,
                () -> encomendaServico.salvar(novaEncomenda(1, DayOfWeek.SUNDAY, LocalTime.of(10, 30), 1)));
    }

    @Test
    public void deveSeguirOFluxoPendenteConfirmadaEntregue () {
        Encomenda encomenda = encomendaServico.salvar(novaEncomenda(1, DayOfWeek.WEDNESDAY, LocalTime.of(15, 0), 1));

        assertEquals(StatusEncomenda.CONFIRMADA, encomendaServico.confirmar(encomenda.getId()).getStatus());
        assertEquals(StatusEncomenda.ENTREGUE, encomendaServico.entregar(encomenda.getId()).getStatus());
    }

    @Test
    public void naoDeveConfirmarEncomendaJaEntregue () {
        assertThrows(RegraNegocioExcecao.class, () -> encomendaServico.confirmar(1));
    }

    @Test
    public void naoDeveExcluirEncomendaEmAberto () {
        assertThrows(RegraNegocioExcecao.class, () -> encomendaServico.excluirPorId(2));
    }
}
