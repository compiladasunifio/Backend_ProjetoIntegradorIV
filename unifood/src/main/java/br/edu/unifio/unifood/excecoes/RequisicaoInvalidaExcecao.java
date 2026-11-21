package br.edu.unifio.unifood.excecoes;

public class RequisicaoInvalidaExcecao extends RuntimeException {
    public RequisicaoInvalidaExcecao (String mensagem) {
        super(mensagem);
    }
}
