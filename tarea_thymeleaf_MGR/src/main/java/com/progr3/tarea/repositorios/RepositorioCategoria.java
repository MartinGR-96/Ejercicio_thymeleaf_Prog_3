package com.progr3.tarea.repositorios;

import com.progr3.tarea.entidades.Categoria;
import com.progr3.tarea.entidades.Estudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioCategoria extends JpaRepository<Categoria, Long> {
    @Query(value = "SELECT * FROM categorias WHERE categorias.activo = true", nativeQuery = true)
    List<Categoria> findAll();

    @Query(value = "SELECT * FROM categorias WHERE categorias.id = :id AND categorias.activo = true", nativeQuery = true)
    Optional<Categoria> findById(@Param("id") long id);

}