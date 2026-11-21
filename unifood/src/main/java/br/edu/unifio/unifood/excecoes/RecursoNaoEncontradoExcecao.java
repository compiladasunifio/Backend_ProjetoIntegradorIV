package br.edu.unifio.unifood.excecoes;

public class RecursoNaoEncontradoExcecao extends RuntimeException {
    public RecursoNaoEncontradoExcecao (String mensagem) {
        super(mensagem);
    }
}
