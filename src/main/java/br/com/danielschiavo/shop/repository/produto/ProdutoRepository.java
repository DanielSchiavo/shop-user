package br.com.danielschiavo.shop.repository.produto;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProduto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	Page<Produto> findAll(Pageable pageable);

	Page<Produto> findAllByAtivoTrue(Pageable pageable);

	@Query("SELECT f FROM Produto p JOIN p.arquivosProduto f WHERE p.id = :produtoId AND f.posicao = :posicao")
	Optional<ArquivoProduto> findArquivosProdutoByProdutoIdAndPosicao(@Param("posicao") Integer posicao,
			@Param("produtoId") Long produtoId);

	@Modifying
	@Query("UPDATE Produto p SET p.arquivosProduto = NULL WHERE p.id = :produtoId")
	void deleteArquivosProdutoByProdutoId(@Param("produtoId") Long produtoId);

	@Query("SELECT p FROM Produto p WHERE p.id IN :ids AND p.ativo = true")
	List<Produto> findAllByIdAndAtivoTrue(@Param("ids") List<Long> ids);
	
	Optional<Produto> findByIdAndAtivoTrue(Long id);

	@Query("SELECT p FROM Produto p WHERE LOWER(p.nome) = LOWER(:nome)")
	Optional<Produto> findByNomeLowerCase(String nome);

	List<Produto> findAllByIdIn(List<Long> produtosId);

}
