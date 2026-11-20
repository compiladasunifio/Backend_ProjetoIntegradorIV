package br.edu.unifio.unifood.repositorios;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.unifio.unifood.entidades.DiaSemana;
import br.edu.unifio.unifood.entidades.HorarioAtendimento;

public interface HorarioAtendimentoRepositorio extends JpaRepository<HorarioAtendimento, Integer> {
    List<HorarioAtendimento> findByVendedorId (Integer vendedorId);

    // Consulta JPQL: conta os horários do mesmo vendedor, no mesmo dia, que se sobrepõem
    // ao intervalo [inicio, fim). O idIgnorado serve para não comparar o horário com ele mesmo na edição.
    @Query ("""
            select count(h) from HorarioAtendimento h
            where h.vendedor.id = :vendedorId
              and h.dia = :dia
              and h.inicio < :fim
              and h.fim > :inicio
              and h.id <> :idIgnorado
            """)
    long contarConflitos (@Param ("vendedorId") Integer vendedorId,
                          @Param ("dia") DiaSemana dia,
                          @Param ("inicio") LocalTime inicio,
                          @Param ("fim") LocalTime fim,
                          @Param ("idIgnorado") Integer idIgnorado);

    // Método derivado: o vendedor atende neste dia e hora? (inicio <= hora <= fim)
    boolean existsByVendedorIdAndDiaAndInicioLessThanEqualAndFimGreaterThanEqual (Integer vendedorId, DiaSemana dia, LocalTime hora1, LocalTime hora2);

    void deleteByVendedorId (Integer vendedorId);
}
