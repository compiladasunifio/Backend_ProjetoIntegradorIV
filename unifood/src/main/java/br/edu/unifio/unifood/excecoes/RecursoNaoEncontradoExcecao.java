package br.edu.unifio.unifood.excecoes;

// Lançada quando o registro procurado não existe (HTTP 404).
public class RecursoNaoEncontradoExcecao extends RuntimeException {
    public RecursoNaoEncontradoExcecao (String mensagem) {
        super(mensagem);
    }
}
