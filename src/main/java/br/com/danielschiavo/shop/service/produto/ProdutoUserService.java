package br.com.danielschiavo.shop.service.produto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.repository.produto.ProdutoRepository;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
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
	
	public Page<MostrarProdutosDTO> listarProdutos(Pageable pageable) {
		Page<Produto> pageProdutos = produtoRepository.findAll(pageable);
		
	    List<MostrarProdutosDTO> listaMostrarProdutosDTO = pageProdutos.getContent().stream()
	            .map(produto -> produtoMapper.produtoParaMostrarProdutosDTO(produto, fileStorageProdutoService, produtoUtilidadeService))
	            .collect(Collectors.toList());

	    return new PageImpl<>(listaMostrarProdutosDTO, pageable, pageProdutos.getTotalElements());
	}
	
	public DetalharProdutoDTO detalharProdutoPorId(Long id) {
		Produto produto = produtoUtilidadeService.pegarProdutoPorId(id);
		return produtoMapper.produtoParaDetalharProdutoDTO(produto, fileStorageProdutoService, produtoUtilidadeService);
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------
	
}
