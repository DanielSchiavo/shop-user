package br.com.danielschiavo.shop.repository.produto;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.danielschiavo.shop.model.produto.subcategoria.SubCategoria;

public interface SubCategoriaRepository extends JpaRepository<SubCategoria, Long>{
	
	Page<SubCategoria> findAll(Pageable pageable);

    @Query("SELECT sc FROM SubCategoria sc WHERE LOWER(sc.nome) = LOWER(:novoNome)")
	Optional<SubCategoria> findByNomeLowerCase(String novoNome);

    @Query("SELECT sc FROM SubCategoria sc WHERE LOWER(sc.nome) = LOWER(:novoNome) AND LOWER(sc.nome) <> LOWER(:nomeSubCategoria)")
	Optional<SubCategoria> findByNomeLowerCaseQueNaoSejaONomeDaCategoriaAtual(String novoNome, String nomeSubCategoria);

}
