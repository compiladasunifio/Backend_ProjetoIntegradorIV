package br.edu.unifio.unifood.recursos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PatchMapping;

import br.edu.unifio.unifood.entidades.Encomenda;
import br.edu.unifio.unifood.entidades.StatusEncomenda;
import br.edu.unifio.unifood.servicos.EncomendaServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping ("encomendas")
public class EncomendaRecurso {
    @Autowired
    private EncomendaServico encomendaServico;

    @GetMapping
    public List<Encomenda> listar () {
        return encomendaServico.listar();
    }

    @GetMapping ("status/{status}")
    public List<Encomenda> listarPorStatus (@PathVariable StatusEncomenda status) {
        return encomendaServico.listarPorStatus(status);
    }

    @GetMapping ("{id}")
    public Encomenda buscarPorId (@PathVariable Integer id) {
        return encomendaServico.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public Encomenda salvar (@Valid @RequestBody Encomenda encomenda) {
        return encomendaServico.salvar(encomenda);
    }

    @PutMapping ("{id}")
    public Encomenda atualizar (@PathVariable Integer id, @Valid @RequestBody Encomenda encomenda) {
        return encomendaServico.atualizar(id, encomenda);
    }

    @PatchMapping ("{id}/confirmar")
    public Encomenda confirmar (@PathVariable Integer id) {
        return encomendaServico.confirmar(id);
    }

    @PatchMapping ("{id}/entregar")
    public Encomenda entregar (@PathVariable Integer id) {
        return encomendaServico.entregar(id);
    }

    @PatchMapping ("{id}/cancelar")
    public Encomenda cancelar (@PathVariable Integer id) {
        return encomendaServico.cancelar(id);
    }

    @DeleteMapping ("{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void excluirPorId (@PathVariable Integer id) {
        encomendaServico.excluirPorId(id);
    }
}
