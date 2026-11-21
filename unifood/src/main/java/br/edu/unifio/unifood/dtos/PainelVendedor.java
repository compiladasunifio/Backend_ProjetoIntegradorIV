package br.edu.unifio.unifood.dtos;

import java.math.BigDecimal;

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
