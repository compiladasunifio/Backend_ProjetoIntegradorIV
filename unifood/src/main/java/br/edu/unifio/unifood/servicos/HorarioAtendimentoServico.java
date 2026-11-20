package br.edu.unifio.unifood.servicos;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.unifood.entidades.HorarioAtendimento;
import br.edu.unifio.unifood.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.unifood.excecoes.RegraNegocioExcecao;
import br.edu.unifio.unifood.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.unifood.repositorios.HorarioAtendimentoRepositorio;

@Service
public class HorarioAtendimentoServico {
    // Ordem da semana (SEGUNDA, TERCA, ...) e depois pelo horário de início
    private static final Comparator<HorarioAtendimento> ORDEM_SEMANA =
            Comparator.comparing(HorarioAtendimento::getDia).thenComparing(HorarioAtendimento::getInicio);

    @Autowired
    private HorarioAtendimentoRepositorio horarioRepositorio;

    @Autowired
    private VendedorServico vendedorServico;

    public List<HorarioAtendimento> listar () {
        return horarioRepositorio.findAll().stream()
                .sorted(Comparator.comparing((HorarioAtendimento h) -> h.getVendedor().getId()).thenComparing(ORDEM_SEMANA))
                .toList();
    }

    public List<HorarioAtendimento> listarPorVendedor (Integer vendedorId) {
        vendedorServico.buscarPorId(vendedorId);
        return horarioRepositorio.findByVendedorId(vendedorId).stream().sorted(ORDEM_SEMANA).toList();
    }

    public HorarioAtendimento buscarPorId (Integer id) {
        return horarioRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Horário de atendimento de id " + id + " não encontrado"));
    }

    @Transactional
    public HorarioAtendimento salvar (HorarioAtendimento horario) {
        horario.setId(null);
        if (horario.getVendedor().getId() == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do vendedor. Ex.: \"vendedor\": {\"id\": 1}");
        }
        horario.setVendedor(vendedorServico.buscarPorId(horario.getVendedor().getId()));
        validar(horario, 0);
        return horarioRepositorio.save(horario);
    }

    // Na edição o horário continua do mesmo vendedor
    @Transactional
    public HorarioAtendimento atualizar (Integer id, HorarioAtendimento dados) {
        HorarioAtendimento horario = buscarPorId(id);
        horario.setDia(dados.getDia());
        horario.setInicio(dados.getInicio());
        horario.setFim(dados.getFim());
        horario.setLocal(dados.getLocal());
        validar(horario, id);
        return horarioRepositorio.save(horario);
    }

    @Transactional
    public void excluirPorId (Integer id) {
        horarioRepositorio.delete(buscarPorId(id));
    }

    private void validar (HorarioAtendimento horario, Integer idIgnorado) {
        if (!horario.getFim().isAfter(horario.getInicio())) {
            throw new RequisicaoInvalidaExcecao("O horário de fim deve ser depois do horário de início");
        }
        // Regra: o vendedor não pode estar em dois lugares ao mesmo tempo
        long conflitos = horarioRepositorio.contarConflitos(horario.getVendedor().getId(), horario.getDia(),
                horario.getInicio(), horario.getFim(), idIgnorado);
        if (conflitos > 0) {
            throw new RegraNegocioExcecao("O vendedor já possui um horário de atendimento que se sobrepõe a "
                    + horario.getDia() + " das " + horario.getInicio() + " às " + horario.getFim());
        }
    }
}
