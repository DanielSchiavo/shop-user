package br.com.danielschiavo.shop.repository.produto;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.danielschiavo.shop.model.produto.categoria.Categoria;

public interface CategoriaRepository extends JpaRepository <Categoria, Long> {

	Page<Categoria> findAll(Pageable pageable);
	
    @Query("SELECT c FROM Categoria c WHERE LOWER(c.nome) = LOWER(:novoNome)")
    Optional<Categoria> findByNomeLowerCase(String novoNome);

}
