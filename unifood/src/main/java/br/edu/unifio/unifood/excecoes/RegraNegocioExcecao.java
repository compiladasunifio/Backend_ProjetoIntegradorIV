package br.edu.unifio.unifood.excecoes;

public class RegraNegocioExcecao extends RuntimeException {
    public RegraNegocioExcecao (String mensagem) {
        super(mensagem);
    }
}
