package br.edu.unifio.unifood.excecoes;

// Lançada quando a operação viola uma regra de negócio do UniFood (HTTP 409).
public class RegraNegocioExcecao extends RuntimeException {
    public RegraNegocioExcecao (String mensagem) {
        super(mensagem);
    }
}
