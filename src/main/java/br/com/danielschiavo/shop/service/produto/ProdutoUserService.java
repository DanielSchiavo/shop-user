package br.com.danielschiavo.shop.service.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.mapper.produto.ProdutoMapper;
import br.com.danielschiavo.repository.produto.ProdutoRepository;
import br.com.danielschiavo.service.produto.CategoriaUtilidadeService;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.services.filestorage.FileStorageProdutoService;
import lombok.Setter;

@Service
@Setter
public class ProdutoUserService {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private FileStorageProdutoService fileStorageProdutoService;

	@Autowired
	private ProdutoMapper produtoMapper;
	
	@Autowired
	private ProdutoUtilidadeService produtoUtilidadeService;
	
	@Autowired
	private CategoriaUtilidadeService categoriaUtilidadeService;
	
	public Page<MostrarProdutosDTO> listarProdutos(Pageable pageable) {
		Page<Produto> pageProdutos = produtoRepository.findAll(pageable);
		return produtoMapper.pageProdutosParaPageMostrarProdutosDTO(pageProdutos, fileStorageProdutoService, produtoUtilidadeService, produtoMapper, categoriaUtilidadeService);
	}
	
	public DetalharProdutoDTO detalharProdutoPorId(Long id) {
		Produto produto = produtoUtilidadeService.verificarSeProdutoExistePorId(id);
		return produtoMapper.produtoParaDetalharProdutoDTO(produto, fileStorageProdutoService, produtoUtilidadeService, categoriaUtilidadeService);
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------
	
}
