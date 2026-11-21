package br.edu.unifio.unifood.servicos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.unifood.dtos.PainelVendedor;
import br.edu.unifio.unifood.entidades.Vendedor;
import br.edu.unifio.unifood.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.unifood.excecoes.RegraNegocioExcecao;

@SpringBootTest
@Transactional
public class VendedorServicoTests {
    @Autowired
    private VendedorServico vendedorServico;

    @Test
    public void deveGerarOPainelDoVendedor () {
        PainelVendedor painel = vendedorServico.gerarPainel(1);

        assertEquals("Doces da Isa", painel.nomeNegocio());
        assertEquals(2, painel.produtosDisponiveis());
        assertEquals(4.5, painel.notaMedia());
        assertEquals(2, painel.totalAvaliacoes());
        assertEquals(4.0, painel.horasAtendimentoSemana());
        assertEquals(1, painel.encomendasPendentes());
        assertEquals(0, new BigDecimal("24.00").compareTo(painel.faturamentoEntregue()));
    }

    @Test
    public void naoDeveCadastrarVendedorComEmailRepetido () {
        Vendedor vendedor = new Vendedor();
        vendedor.setNome("Outra Isa");
        vendedor.setEmail("ISA@unifood.com");
        vendedor.setTelefone("14990000000");
        vendedor.setNomeNegocio("Outro negócio");
        vendedor.setLocalizacao("Bloco 1");

        assertThrows(RegraNegocioExcecao.class, () -> vendedorServico.salvar(vendedor));
    }

    @Test
    public void naoDeveExcluirVendedorComEncomendasEmAberto () {
        assertThrows(RegraNegocioExcecao.class, () -> vendedorServico.excluirPorId(1));
    }

    @Test
    public void deveExcluirVendedorSemEncomendasEmAbertoComProdutosEHorarios () {
        vendedorServico.excluirPorId(3);

        assertThrows(RecursoNaoEncontradoExcecao.class, () -> vendedorServico.buscarPorId(3));
    }

    @Test
    public void deveRetornarNaoEncontradoParaVendedorInexistente () {
        assertThrows(RecursoNaoEncontradoExcecao.class, () -> vendedorServico.buscarPorId(999));
    }
}
