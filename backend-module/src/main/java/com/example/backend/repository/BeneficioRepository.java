package com.example.backend.repository;

import com.example.backend.entity.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    List<Beneficio> findAllByAtivoTrue();

    Optional<Beneficio> findByIdAndAtivoTrue(Long id);

    /**
     * Atualiza diretamente a coluna {@code ativo} via UPDATE sem carregar a entidade.
     *
     * <p>Bypass intencional do {@code @Version}: alternar um flag booleano é
     * idempotente e de baixo risco concorrente, portanto o tradeoff é aceitável.
     * Retorna o número de linhas afetadas (0 = ID não encontrado, 1 = sucesso).
     */
    @Modifying
    @Query("UPDATE Beneficio b SET b.ativo = :ativo WHERE b.id = :id")
    int updateAtivo(@Param("id") Long id, @Param("ativo") Boolean ativo);
}
