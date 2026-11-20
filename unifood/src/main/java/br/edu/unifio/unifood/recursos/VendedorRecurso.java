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

import org.springframework.web.bind.annotation.RequestParam;

import br.edu.unifio.unifood.dtos.PainelVendedor;
import br.edu.unifio.unifood.entidades.Encomenda;
import br.edu.unifio.unifood.entidades.HorarioAtendimento;
import br.edu.unifio.unifood.entidades.Produto;
import br.edu.unifio.unifood.entidades.Vendedor;
import br.edu.unifio.unifood.servicos.EncomendaServico;
import br.edu.unifio.unifood.servicos.HorarioAtendimentoServico;
import br.edu.unifio.unifood.servicos.ProdutoServico;
import br.edu.unifio.unifood.servicos.VendedorServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping ("vendedores")
public class VendedorRecurso {
    @Autowired
    private VendedorServico vendedorServico;

    @Autowired
    private ProdutoServico produtoServico;

    @Autowired
    private HorarioAtendimentoServico horarioServico;

    @Autowired
    private EncomendaServico encomendaServico;

    @GetMapping
    public List<Vendedor> listar () {
        return vendedorServico.listar();
    }

    // Ex.: /vendedores/busca?negocio=doces
    @GetMapping ("busca")
    public List<Vendedor> buscarPorNomeNegocio (@RequestParam String negocio) {
        return vendedorServico.buscarPorNomeNegocio(negocio);
    }

    @GetMapping ("{id}")
    public Vendedor buscarPorId (@PathVariable Integer id) {
        return vendedorServico.buscarPorId(id);
    }

    // "Meus produtos"
    @GetMapping ("{id}/produtos")
    public List<Produto> listarProdutos (@PathVariable Integer id) {
        return produtoServico.listarPorVendedor(id);
    }

    // Horários de atendimento em ordem de dia e hora
    @GetMapping ("{id}/horarios")
    public List<HorarioAtendimento> listarHorarios (@PathVariable Integer id) {
        return horarioServico.listarPorVendedor(id);
    }

    // Agenda de retiradas do vendedor
    @GetMapping ("{id}/encomendas")
    public List<Encomenda> listarEncomendas (@PathVariable Integer id) {
        return encomendaServico.listarPorVendedor(id);
    }

    // Painel com os indicadores do negócio
    @GetMapping ("{id}/painel")
    public PainelVendedor gerarPainel (@PathVariable Integer id) {
        return vendedorServico.gerarPainel(id);
    }

    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public Vendedor salvar (@Valid @RequestBody Vendedor vendedor) {
        return vendedorServico.salvar(vendedor);
    }

    @PutMapping ("{id}")
    public Vendedor atualizar (@PathVariable Integer id, @Valid @RequestBody Vendedor vendedor) {
        return vendedorServico.atualizar(id, vendedor);
    }

    @DeleteMapping ("{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void excluirPorId (@PathVariable Integer id) {
        vendedorServico.excluirPorId(id);
    }
}
