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

import br.edu.unifio.unifood.entidades.HorarioAtendimento;
import br.edu.unifio.unifood.servicos.HorarioAtendimentoServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping ("horarios")
public class HorarioAtendimentoRecurso {
    @Autowired
    private HorarioAtendimentoServico horarioServico;

    @GetMapping
    public List<HorarioAtendimento> listar () {
        return horarioServico.listar();
    }

    @GetMapping ("{id}")
    public HorarioAtendimento buscarPorId (@PathVariable Integer id) {
        return horarioServico.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public HorarioAtendimento salvar (@Valid @RequestBody HorarioAtendimento horario) {
        return horarioServico.salvar(horario);
    }

    @PutMapping ("{id}")
    public HorarioAtendimento atualizar (@PathVariable Integer id, @Valid @RequestBody HorarioAtendimento horario) {
        return horarioServico.atualizar(id, horario);
    }

    @DeleteMapping ("{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void excluirPorId (@PathVariable Integer id) {
        horarioServico.excluirPorId(id);
    }
}
