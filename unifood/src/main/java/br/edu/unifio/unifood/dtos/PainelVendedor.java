package br.edu.unifio.unifood.dtos;

import java.math.BigDecimal;

// Resumo do negócio do vendedor (tela "Painel" do front-end).
// Não é uma entidade: é calculado a partir de Produto, Avaliacao, HorarioAtendimento e Encomenda.
public record PainelVendedor (
        Integer vendedorId,
        String nomeNegocio,
        long totalProdutos,
        long produtosDisponiveis,
        Double notaMedia,
        long totalAvaliacoes,
        double horasAtendimentoSemana,
        long encomendasPendentes,
        long encomendasConfirmadas,
        long encomendasEntregues,
        BigDecimal faturamentoEntregue) {
}
