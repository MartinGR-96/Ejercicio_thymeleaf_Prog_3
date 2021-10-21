package com.progr3.tarea.repositorios;

import com.progr3.tarea.entidades.Estudio;
import com.progr3.tarea.entidades.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioEstudio extends JpaRepository<Estudio, Long> {
    @Query(value = "SELECT * FROM estudios WHERE estudios.activo = true", nativeQuery = true)
    List<Estudio> findAll();

    @Query(value = "SELECT * FROM estudios WHERE estudios.id = :id AND estudios.activo = true", nativeQuery = true)
    Optional<Estudio> findById(@Param("id") long id);

}