package br.edu.unifio.unifood.repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.edu.unifio.unifood.entidades.DiaSemana;

@SpringBootTest
public class HorarioAtendimentoRepositorioTests {
    @Autowired
    private HorarioAtendimentoRepositorio horarioRepositorio;

    @Test
    public void deveEncontrarConflitoDeHorario () {
        long conflitos = horarioRepositorio.contarConflitos(1, DiaSemana.SEGUNDA, LocalTime.of(11, 0), LocalTime.of(13, 0), 0);
        assertEquals(1, conflitos);
    }

    @Test
    public void naoDeveConsiderarConflitoHorarioQueComecaQuandoOOutroTermina () {
        long conflitos = horarioRepositorio.contarConflitos(1, DiaSemana.SEGUNDA, LocalTime.of(12, 0), LocalTime.of(13, 0), 0);
        assertEquals(0, conflitos);
    }

    @Test
    public void deveVerificarSeOVendedorAtendeNoHorario () {
        LocalTime dezEMeia = LocalTime.of(10, 30);
        LocalTime tresDaTarde = LocalTime.of(15, 0);

        assertTrue(horarioRepositorio.existsByVendedorIdAndDiaAndInicioLessThanEqualAndFimGreaterThanEqual(1, DiaSemana.SEGUNDA, dezEMeia, dezEMeia));
        assertFalse(horarioRepositorio.existsByVendedorIdAndDiaAndInicioLessThanEqualAndFimGreaterThanEqual(1, DiaSemana.SEGUNDA, tresDaTarde, tresDaTarde));
    }
}
