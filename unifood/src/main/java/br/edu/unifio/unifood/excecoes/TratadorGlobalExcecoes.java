package br.edu.unifio.unifood.excecoes;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.edu.unifio.unifood.dtos.ErroResposta;
import jakarta.servlet.http.HttpServletRequest;

// Tratamento centralizado dos erros: transforma as exceções em respostas JSON com status HTTP adequado.
@RestControllerAdvice
public class TratadorGlobalExcecoes {

    // 404 - registro não encontrado
    @ExceptionHandler (RecursoNaoEncontradoExcecao.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado (RecursoNaoEncontradoExcecao excecao, HttpServletRequest requisicao) {
        return responder(HttpStatus.NOT_FOUND, excecao.getMessage(), requisicao, null);
    }

    // 409 - conflito com regra de negócio
    @ExceptionHandler (RegraNegocioExcecao.class)
    public ResponseEntity<ErroResposta> tratarRegraNegocio (RegraNegocioExcecao excecao, HttpServletRequest requisicao) {
        return responder(HttpStatus.CONFLICT, excecao.getMessage(), requisicao, null);
    }

    // 400 - dados inválidos conferidos no serviço
    @ExceptionHandler (RequisicaoInvalidaExcecao.class)
    public ResponseEntity<ErroResposta> tratarRequisicaoInvalida (RequisicaoInvalidaExcecao excecao, HttpServletRequest requisicao) {
        return responder(HttpStatus.BAD_REQUEST, excecao.getMessage(), requisicao, null);
    }

    // 400 - Bean Validation (@Valid) falhou: devolve a mensagem de cada campo
    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao (MethodArgumentNotValidException excecao, HttpServletRequest requisicao) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : excecao.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        return responder(HttpStatus.BAD_REQUEST, "Existem campos inválidos na requisição", requisicao, campos);
    }

    // 400 - JSON mal formatado ou valor em formato errado (ex.: data "31/02", dia "DOMINGO", hora "25:00")
    @ExceptionHandler (HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarJsonInvalido (HttpMessageNotReadableException excecao, HttpServletRequest requisicao) {
        return responder(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido. Confira o formato do JSON e dos campos (datas: aaaa-mm-dd, horas: HH:mm, dia: SEGUNDA a SABADO)",
                requisicao, null);
    }

    // 400 - parâmetro da URL com tipo errado (ex.: /produtos/abc, status inexistente)
    @ExceptionHandler (MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> tratarTipoInvalido (MethodArgumentTypeMismatchException excecao, HttpServletRequest requisicao) {
        return responder(HttpStatus.BAD_REQUEST,
                "Valor inválido para o parâmetro '" + excecao.getName() + "': " + excecao.getValue(), requisicao, null);
    }

    // 400 - parâmetro obrigatório da URL não informado
    @ExceptionHandler (MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResposta> tratarParametroAusente (MissingServletRequestParameterException excecao, HttpServletRequest requisicao) {
        return responder(HttpStatus.BAD_REQUEST,
                "O parâmetro '" + excecao.getParameterName() + "' é obrigatório", requisicao, null);
    }

    // 409 - restrição do banco (ex.: chave estrangeira ou valor único) que escapou das regras do serviço
    @ExceptionHandler (DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratarIntegridade (DataIntegrityViolationException excecao, HttpServletRequest requisicao) {
        return responder(HttpStatus.CONFLICT,
                "A operação viola a integridade dos dados (registro duplicado ou em uso por outro registro)", requisicao, null);
    }

    private ResponseEntity<ErroResposta> responder (HttpStatus status, String mensagem, HttpServletRequest requisicao, Map<String, String> campos) {
        ErroResposta corpo = new ErroResposta(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                mensagem, requisicao.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
