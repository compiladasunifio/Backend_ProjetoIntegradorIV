package br.edu.unifio.unifood.excecoes;

// Lançada quando os dados enviados são inválidos e não dá para conferir só com anotações (HTTP 400).
// Ex.: horário de fim antes do início, relacionamento enviado sem id.
public class RequisicaoInvalidaExcecao extends RuntimeException {
    public RequisicaoInvalidaExcecao (String mensagem) {
        super(mensagem);
    }
}
